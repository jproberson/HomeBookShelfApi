package com.example.homebookshelfapi.services.impl

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

    override fun fetchMoreRecommendations(userId: UUID): ResponseEntity<List<BookEntity>> {
        val user = userService.getUserById(userId)
        if (user == null) {
            logger.error("User not found with id: $userId when fetching more recommendations")
            return ResponseEntity.badRequest().body(null)
        }

        val existingRecommendations = bookRecommendationRepository.findByUserId(userId)
        val newRecommendationsResponse = getRecommendations(userId, fetchMore = true)

        if (newRecommendationsResponse.statusCode.is4xxClientError) {
            return newRecommendationsResponse
        } else {
            val newRecommendations = newRecommendationsResponse.body ?: emptyList()

            val combinedRecommendations = (existingRecommendations.map { it.book } + newRecommendations).distinctBy { it.id }
            return ResponseEntity.ok(combinedRecommendations)
        }
    }

    override fun getRecommendations(userId: UUID, fetchMore: Boolean): ResponseEntity<List<BookEntity>> {
        val user = userService.getUserById(userId)
        if (user == null) {
            logger.error("User not found with id: $userId when fetching recommendations")
            return ResponseEntity.badRequest().body(null)
        }

        val existingRecommendations = bookRecommendationRepository.findByUserId(userId)
        if (existingRecommendations.isEmpty() || fetchMore) {
            val userBooks = userBooksService.getUserBooks(userId)
            if (userBooks.isEmpty() || userBooks.size < 3) {
                logger.warn("User has insufficient books to generate recommendations")
                return ResponseEntity.badRequest().body(emptyList())
            }
            val userBookTitles = userBooks.map { it.title }
            val currentRecommendedBookTitles = existingRecommendations.map { it.book.title }
            val combinedBooks = userBookTitles + currentRecommendedBookTitles
            return fetchAndSaveRecommendations(user, combinedBooks)
        }

        return ResponseEntity.ok(existingRecommendations.map { it.book })
    }

    private fun fetchAndSaveRecommendations(
        user: UserEntity,
        bookTitles: List<String>
    ): ResponseEntity<List<BookEntity>> {
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
        return ResponseEntity.ok(books)
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
