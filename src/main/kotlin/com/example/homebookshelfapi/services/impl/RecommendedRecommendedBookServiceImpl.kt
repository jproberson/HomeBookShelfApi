package com.example.homebookshelfapi.services.impl

import com.example.homebookshelfapi.domain.dto.RecommendationResponse
import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.domain.entities.RecommendedBooksEntity
import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.exceptions.UserNotFoundException
import com.example.homebookshelfapi.external.gpt.GptService
import com.example.homebookshelfapi.repositories.RecommendedBooksRepository
import com.example.homebookshelfapi.services.BookService
import com.example.homebookshelfapi.services.RecommendedBookService
import com.example.homebookshelfapi.services.UserBooksService
import com.example.homebookshelfapi.services.UsersService
import com.example.homebookshelfapi.utils.logger
import org.springframework.stereotype.Service

@Service
class RecommendedRecommendedBookServiceImpl(
    private val userBooksService: UserBooksService,
    private val userService: UsersService,
    private val gptService: GptService,
    private val bookService: BookService,
    private val recommendedBooksRepository: RecommendedBooksRepository
) : RecommendedBookService {

    private val logger = logger<RecommendedRecommendedBookServiceImpl>()

    fun isRecommendationsAvailable(): Boolean {
        return gptService.isAvailable()
    }

    override fun getRecommendationsForUser(
        username: String,
        fetchMore: Boolean
    ): RecommendationResponse {
        val user = userService.getByUsername(username)
        if (user == null) {
            logger.error("User not found with id: $username when fetching recommendations")
            throw UserNotFoundException("User not found with id: $username")
        }

        val existingRecommendations = recommendedBooksRepository.findByUser(user)

        if (existingRecommendations.isEmpty() || fetchMore) {
            val userBooks = userBooksService.getUserBooks(user.username)
            if (userBooks.isEmpty() || userBooks.size < 3) {
                logger.warn("User has insufficient books to generate recommendations")
                return RecommendationResponse(emptyList(), isRecommendationsAvailable())
            }
            val combinedBooks =
                (userBooks.map { it.title } + existingRecommendations.map { it.book.title }).distinct()
            val newRecommendations = fetchAndSaveRecommendations(user, combinedBooks)
            val combinedRecommendations =
                (existingRecommendations.map { it.book } + newRecommendations).distinctBy { it.id }

            return RecommendationResponse(combinedRecommendations, isRecommendationsAvailable())

        }

        return RecommendationResponse(existingRecommendations.map { it.book }, isRecommendationsAvailable())

    }

    private fun fetchAndSaveRecommendations(
        user: UserEntity,
        bookTitles: List<String>
    ): List<BookEntity> {
        println("Starting fetchAndSaveRecommendations for user: ${user.id}")

        val recommendedIsbns = gptService.getBookRecommendations(bookTitles).body ?: emptyList()
        logger.info("Received recommended ISBNS: $recommendedIsbns")

        val books =
            recommendedIsbns
                .mapNotNull { isbn ->
                    logger.info("Processing book with ISBN: $isbn")
                    bookService.addBookByIsbn(isbn)?.also { println("Book found/added: ${it.id}") }
                }
                .filter { book -> !bookTitles.contains(book.title) }
                .map { book ->
                    saveRecommendation(user, book)
                    book
                }

        println("Finished processing books: ${books.map { it.id }}")
        return books
    }

    private fun saveRecommendation(user: UserEntity, book: BookEntity): RecommendedBooksEntity {
        println("Saving recommendation for user: ${user.id}, book: ${book.id}")
        return recommendedBooksRepository
            .save(RecommendedBooksEntity(user = user, book = book, recommendationStrategy = "gpt"))
            .also { println("Recommendation saved for book: ${book.id}") }
    }

    override fun removeRecommendedBookForUser(
        username: String,
        savedBook: BookEntity
    ): BookEntity? {
        val user = userService.getByUsername(username)
        if (user == null) {
            logger.error("User not found with id: $username when fetching recommendations")
            throw UserNotFoundException("User not found with id: $username")
        }
        val userRecommendations = recommendedBooksRepository.findByUser(user)
        val recommendedBook = userRecommendations.firstOrNull { it.book.id == savedBook.id }
        if (recommendedBook != null) {
            recommendedBooksRepository.delete(recommendedBook)
            logger.info("Removed recommended book: ${savedBook.title} for user: $username")
            return savedBook
        } else {
            logger.warn(
                "Book: ${savedBook.title} was not found in the recommendations for user: $username"
            )
            return null
        }
    }
}
