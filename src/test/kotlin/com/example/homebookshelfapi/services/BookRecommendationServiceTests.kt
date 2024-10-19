package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.external.gpt.GptService
import com.example.homebookshelfapi.repositories.BookRecommendationRepository
import com.example.homebookshelfapi.services.impl.BookRecommendationServiceImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import testBookEntity
import testRecommendedBooksEntity
import testUserEntity

class BookRecommendationServiceTests {
    @MockK
    private lateinit var userBooksService: UserBooksService

    @MockK
    private lateinit var userService: UsersService

    @MockK
    private lateinit var gptService: GptService

    @MockK
    private lateinit var bookService: BookService

    @MockK
    private lateinit var bookRecommendationRepository: BookRecommendationRepository

    @InjectMockKs
    private lateinit var bookRecommendationService: BookRecommendationServiceImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }


    @Test
    fun `getRecommendationsForUser returns a list of book recommendations for a user`() {
        val user = testUserEntity()
        val existingRecommendations = listOf(testRecommendedBooksEntity())

        every { userService.getByUsername(user.username) } returns user
        every { bookRecommendationRepository.findByUser(user) } returns existingRecommendations
        every { gptService.isAvailable() } returns true

        val result = bookRecommendationService.getRecommendationsForUser(user.username, fetchMore = false)
        assertEquals(200, result.statusCode.value())
        assertEquals(1, result.body?.books?.size)
        assertEquals(existingRecommendations[0].book.isbn, result.body?.books?.get(0)?.isbn)
    }

    @Test
    fun `getRecommendationsForUser returns a bad request if user not found`() {
        val user = testUserEntity()

        every { userService.getByUsername(user.username) } returns null
        every { gptService.isAvailable() } returns true

        val result = bookRecommendationService.getRecommendationsForUser(user.username, fetchMore = false)

        assertEquals(400, result.statusCode.value())
    }

    @Test
    fun `getRecommendationsForUser returns a bad request if user has insufficient books`() {
        val user = testUserEntity()

        every { gptService.isAvailable() } returns true
        every { userService.getByUsername(user.username) } returns user
        every { bookRecommendationRepository.findByUser(user) } returns emptyList()
        every { userBooksService.getUserBooks(user.username) } returns emptyList()

        val result = bookRecommendationService.getRecommendationsForUser(user.username, fetchMore = false)
        assertEquals(400, result.statusCode.value())
    }

    @Test
    fun `removeRecommendedBookForUser removes a recommended book for a user`() {
        val user = testUserEntity()
        val recommendedBook = testRecommendedBooksEntity()

        every { userService.getByUsername(user.username) } returns user
        every { bookRecommendationRepository.findByUser(user) } returns listOf(recommendedBook)
        every { bookRecommendationRepository.delete(recommendedBook) } returns Unit

        val result = bookRecommendationService.removeRecommendedBookForUser(user.username, recommendedBook.book)
        assertEquals(200, result.statusCode.value())
        assertEquals(recommendedBook.book.isbn, result.body?.isbn)
    }

    @Test
    fun `removeRecommendedBookForUser returns a not found status if book not found in recommendations`() {
        val user = testUserEntity()
        val recommendedBook = testRecommendedBooksEntity()

        every { userService.getByUsername(user.username) } returns user
        every { bookRecommendationRepository.findByUser(user) } returns emptyList()

        val result = bookRecommendationService.removeRecommendedBookForUser(user.username, recommendedBook.book)
        assertEquals(404, result.statusCode.value())
    }

    @Test
    fun `getRecommendationsForUser fetches and saves new recommendations if existing is empty`() {
        val user = testUserEntity()
        val userBooks = listOf(testBookEntity(), testBookEntity(), testBookEntity())
        val newRecommendations = listOf(testBookEntity())

        every { userService.getByUsername(user.username) } returns user
        every { bookRecommendationRepository.findByUser(user) } returns emptyList()
        every { userBooksService.getUserBooks(user.username) } returns userBooks
        every { gptService.getBookRecommendations(any()) } returns ResponseEntity.ok(listOf(newRecommendations[0].isbn))
        every { bookService.addBookByIsbn(any()) } returns newRecommendations[0]
        every { bookRecommendationRepository.save(any()) } returns testRecommendedBooksEntity()
        every { gptService.isAvailable() } returns true

        val result = bookRecommendationService.getRecommendationsForUser(user.username, fetchMore = false)
        assertEquals(200, result.statusCode.value())
        assertEquals(1, result.body?.books?.size)
    }

    @Test
    fun `getRecommendationsForUser fetches more recommendations when fetchMore is true`() {
        val user = testUserEntity()
        val existingRecommendations = listOf(testRecommendedBooksEntity())
        val newRecommendations = listOf(testBookEntity(), testBookEntity())

        every { userService.getByUsername(user.username) } returns user
        every { bookRecommendationRepository.findByUser(user) } returns existingRecommendations
        every { userBooksService.getUserBooks(user.username) } returns listOf(
            testBookEntity(),
            testBookEntity(),
            testBookEntity()
        )
        every { gptService.getBookRecommendations(any()) } returns ResponseEntity.ok(
            listOf(
                newRecommendations[0].isbn
            )
        )
        every { bookService.addBookByIsbn(any()) } returns newRecommendations[0]
        every { bookRecommendationRepository.save(any()) } returns testRecommendedBooksEntity()
        every { gptService.isAvailable() } returns true

        val result = bookRecommendationService.getRecommendationsForUser(user.username, fetchMore = true)
        assertEquals(200, result.statusCode.value())
        assertEquals(2, result.body?.books?.size)
    }

    @Test
    fun `getRecommendationsForUser saves new recommendations when fetchMore is true`() {
        val user = testUserEntity()
        val userBooks = listOf(testBookEntity(), testBookEntity(), testBookEntity())
        val newRecommendations = listOf(testBookEntity())

        every { userService.getByUsername(user.username) } returns user
        every { bookRecommendationRepository.findByUser(user) } returns emptyList()
        every { userBooksService.getUserBooks(user.username) } returns userBooks
        every { gptService.getBookRecommendations(any()) } returns ResponseEntity.ok(listOf(newRecommendations[0].isbn))
        every { bookService.addBookByIsbn(newRecommendations[0].isbn) } returns newRecommendations[0]
        every { bookRecommendationRepository.save(any()) } returns testRecommendedBooksEntity()
        every { gptService.isAvailable() } returns true

        val result = bookRecommendationService.getRecommendationsForUser(user.username, fetchMore = true)
        assertEquals(200, result.statusCode.value())
        assertEquals(1, result.body?.books?.size)
        verify { bookService.addBookByIsbn(newRecommendations[0].isbn) }
    }

    @Test
    fun `getRecommendationsForUser does not fetch new recommendations when fetchMore is false and recommendations exist`() {
        val user = testUserEntity()
        val existingRecommendations = listOf(testRecommendedBooksEntity())

        every { userService.getByUsername(user.username) } returns user
        every { bookRecommendationRepository.findByUser(user) } returns existingRecommendations
        every { gptService.isAvailable() } returns true

        val result = bookRecommendationService.getRecommendationsForUser(user.username, fetchMore = false)
        assertEquals(200, result.statusCode.value())
        assertEquals(1, result.body?.books?.size)
        verify(exactly = 0) { gptService.getBookRecommendations(any()) }
    }

}