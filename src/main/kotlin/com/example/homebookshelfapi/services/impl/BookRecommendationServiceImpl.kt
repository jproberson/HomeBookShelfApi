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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
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

    override fun fetchMoreRecommendations(userId: UUID): Mono<ResponseEntity<List<BookEntity>>> {
        val user = userService.getUserById(userId)
        if (user == null) {
            logger.error("User not found with id: $userId when fetching more recommendations")
            return Mono.just(ResponseEntity.badRequest().body(null))
        }

        val existingRecommendations = bookRecommendationRepository.findByUserId(userId)

        return getRecommendations(userId, fetchMore = true)
            .flatMap { newRecommendationsResponse ->
                if (newRecommendationsResponse.statusCode.is4xxClientError) {
                    Mono.just(newRecommendationsResponse)
                } else {
                    val newRecommendations = newRecommendationsResponse.body ?: emptyList()

                    val combinedRecommendations =
                        (existingRecommendations.map { it.book } + newRecommendations).distinctBy { it.id }

                    Mono.just(ResponseEntity.ok(combinedRecommendations))
                }
            }
    }

    override fun getRecommendations(userId: UUID, fetchMore: Boolean): Mono<ResponseEntity<List<BookEntity>>> {
        val user = userService.getUserById(userId)
        if (user == null) {
            logger.error("User not found with id: $userId when fetching recommendations")
            return Mono.just(ResponseEntity.badRequest().body(null))
        }

        val existingRecommendations = bookRecommendationRepository.findByUserId(userId)
        if (existingRecommendations.isEmpty() || fetchMore) {
            val userBooks = userBooksService.getUserBooks(userId)
            if (userBooks.isEmpty() || userBooks.size < 3) {
                logger.warn("User has insufficient books to generate recommendations")
                return Mono.just(ResponseEntity.badRequest().body(emptyList()))
            }
            return fetchAndSaveRecommendations(user, userBooks.map { it.title })
        }

        return Mono.just(ResponseEntity.ok(existingRecommendations.map { it.book }))
    }

    private fun fetchAndSaveRecommendations(
        user: UserEntity,
        bookTitles: List<String>
    ): Mono<ResponseEntity<List<BookEntity>>> {
        println("Starting fetchAndSaveRecommendations for user: ${user.id}")

        return gptService.getBookRecommendations(bookTitles)
            .doOnSubscribe { println("Fetching recommendations for books: $bookTitles") }
            .flatMapMany { response ->
                val recommendedIsbns = response.body ?: emptyList()
                logger.info("Received recommended ISBNS: $recommendedIsbns")
                Flux.fromIterable(recommendedIsbns)
            }
            .flatMap { isbn ->
                logger.info("Processing book with ISBN: $isbn")
                Mono.justOrEmpty(bookService.addBookByIsbn(isbn)) // add duplicate book titles for now if they do have a different isbn
                    .doOnNext { book -> println("Book found/added: ${book.id}") }
            }
            .filter { book ->
                !bookTitles.contains(book.title) // Filter out books that are already in the user's collection
            }
            .flatMap { book ->
                saveRecommendation(user, book)
                    .doOnSuccess { println("Successfully saved recommendation for book: ${book.id}") }
                    .doOnError { error -> println("Error saving recommendation: ${error.message}") }
                    .thenReturn(book)
            }
            .collectList()
            .map { books ->
                println("Finished processing books: ${books.map { it.id }}")
                ResponseEntity.ok(books)
            }
    }

    private fun saveRecommendation(user: UserEntity, book: BookEntity): Mono<RecommendedBooksEntity> {
        println("Saving recommendation for user: ${user.id}, book: ${book.id}")

        return Mono.fromCallable {
            bookRecommendationRepository.save(
                RecommendedBooksEntity(
                    user = user,
                    book = book,
                    recommendationStrategy = "gpt"
                )
            )
        }
            .subscribeOn(Schedulers.boundedElastic())
            .doOnSuccess { println("Recommendation saved for book: ${book.id}") }
    }
}
