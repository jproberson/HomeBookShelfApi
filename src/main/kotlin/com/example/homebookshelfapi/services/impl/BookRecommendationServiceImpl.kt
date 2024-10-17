package com.example.homebookshelfapi.services.impl

import com.example.homebookshelfapi.domain.dto.RecommendationResponse
import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.domain.entities.RecommendedBooksEntity
import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.external.gpt.GptService
import com.example.homebookshelfapi.repositories.BookRecommendationRepository
import com.example.homebookshelfapi.services.BookRecommendationService
import com.example.homebookshelfapi.services.BookService
import com.example.homebookshelfapi.services.UserBooksService
import com.example.homebookshelfapi.services.UsersService
import com.example.homebookshelfapi.utils.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class BookRecommendationServiceImpl(
    private val userBooksService: UserBooksService,
    private val userService: UsersService,
    private val gptService: GptService,
    private val bookService: BookService,
    private val bookRecommendationRepository: BookRecommendationRepository
) : BookRecommendationService {

    private val logger = logger<BookRecommendationServiceImpl>()

    fun isGptAvailable(): Boolean {
        return gptService.isAvailable()
    }

    override fun fetchMoreRecommendations(userId: UUID): ResponseEntity<RecommendationResponse> {
        val user = userService.getUserById(userId)
        if (user == null) {
            logger.error("User not found with id: $userId when fetching more recommendations")
            return ResponseEntity.badRequest().body(RecommendationResponse(emptyList(), isGptAvailable()))
        }

        val existingRecommendations = bookRecommendationRepository.findByUserId(userId)
        val newRecommendationsResponse = getRecommendations(userId, fetchMore = true)

        if (newRecommendationsResponse.statusCode.is4xxClientError) {
            return newRecommendationsResponse
        } else {
            val newRecommendations = newRecommendationsResponse.body?.books ?: emptyList()

            val combinedRecommendations = (existingRecommendations.map { it.book } + newRecommendations).distinctBy { it.id }
            return ResponseEntity.ok(RecommendationResponse(combinedRecommendations, isGptAvailable()))
        }
    }

    override fun getRecommendations(userId: UUID, fetchMore: Boolean): ResponseEntity<RecommendationResponse> {
        val user = userService.getUserById(userId)
        if (user == null) {
            logger.error("User not found with id: $userId when fetching recommendations")
            return ResponseEntity.badRequest().body(RecommendationResponse(emptyList(), isGptAvailable()))
        }

        val existingRecommendations = bookRecommendationRepository.findByUserId(userId)
        if (existingRecommendations.isEmpty() || fetchMore) {
            val userBooks = userBooksService.getUserBooks(userId)
            if (userBooks.isEmpty() || userBooks.size < 3) {
                logger.warn("User has insufficient books to generate recommendations")
                return ResponseEntity.badRequest().body(RecommendationResponse(emptyList(), isGptAvailable()))
            }
            val userBookTitles = userBooks.map { it.title }
            val currentRecommendedBookTitles = existingRecommendations.map { it.book.title }
            val combinedBooks = userBookTitles + currentRecommendedBookTitles
            return fetchAndSaveRecommendations(user, combinedBooks)
        }

        return ResponseEntity.ok(RecommendationResponse(existingRecommendations.map { it.book }, isGptAvailable()))
    }

    override fun removeRecommendedBookForUser(userId: UUID, savedBook: BookEntity): ResponseEntity<BookEntity?> {
        val userRecommendations = bookRecommendationRepository.findByUserId(userId)
        val recommendedBook = userRecommendations.firstOrNull { it.book.id == savedBook.id }
        if (recommendedBook != null) {
            bookRecommendationRepository.delete(recommendedBook)
            logger.info("Removed recommended book: ${savedBook.title} for user: $userId")
            return ResponseEntity.ok(savedBook)
        } else {
            logger.warn("Book: ${savedBook.title} was not found in the recommendations for user: $userId")
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }

    private fun fetchAndSaveRecommendations(
        user: UserEntity,
        bookTitles: List<String>
    ): ResponseEntity<RecommendationResponse> {
        println("Starting fetchAndSaveRecommendations for user: ${user.id}")

        val recommendedIsbns = gptService.getBookRecommendations(bookTitles).body ?: emptyList()
        logger.info("Received recommended ISBNS: $recommendedIsbns")

        val books = recommendedIsbns.mapNotNull { isbn ->
            logger.info("Processing book with ISBN: $isbn")
            bookService.addBookByIsbn(isbn)?.also {
                println("Book found/added: ${it.id}")
            }
        }.filter { book ->
            !bookTitles.contains(book.title)
        }.map { book ->
            saveRecommendation(user, book)
            book
        }

        println("Finished processing books: ${books.map { it.id }}")
        return ResponseEntity.ok(RecommendationResponse(books, isGptAvailable()))
    }

    private fun saveRecommendation(user: UserEntity, book: BookEntity): RecommendedBooksEntity {
        println("Saving recommendation for user: ${user.id}, book: ${book.id}")
        return bookRecommendationRepository.save(
            RecommendedBooksEntity(
                user = user,
                book = book,
                recommendationStrategy = "gpt"
            )
        ).also {
            println("Recommendation saved for book: ${book.id}")
        }
    }
}
