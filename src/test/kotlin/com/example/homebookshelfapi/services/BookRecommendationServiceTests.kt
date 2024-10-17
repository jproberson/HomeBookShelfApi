package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.external.gpt.GptService
import com.example.homebookshelfapi.repositories.BookRecommendationRepository
import com.example.homebookshelfapi.services.impl.BookRecommendationServiceImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
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
    fun `fetchMoreRecommendations returns a new list of book recommendations for a user if none existing`() {
        val user = testUserEntity()
        val recommendedBooks = listOf(testBookEntity(), testBookEntity())

        every { userService.getUserById(user.id) } returns user
        every { bookRecommendationRepository.findByUserId(user.id) } returns emptyList()
        every { userBooksService.getUserBooks(user.id) } returns listOf(
            testBookEntity(),
            testBookEntity(),
            testBookEntity()
        )
        every { bookService.addBookByIsbn(recommendedBooks[0].isbn) } returns testBookEntity(isbn = recommendedBooks[0].isbn)
        every { bookService.addBookByIsbn(recommendedBooks[1].isbn) } returns testBookEntity(isbn = recommendedBooks[1].isbn)
        every { gptService.isAvailable() } returns true

        every { gptService.getBookRecommendations(any()) } returns
            ResponseEntity.ok(listOf(recommendedBooks[0].isbn, recommendedBooks[1].isbn))

        every { bookRecommendationRepository.save(any()) } returns testRecommendedBooksEntity()


        val result = bookRecommendationService.fetchMoreRecommendations(user.id)
        assertEquals(200, result.statusCode.value())
        assertEquals(2, result.body?.books?.size)
        assertEquals(recommendedBooks[0].isbn, result.body?.books?.get(0)?.isbn)
        assertEquals(recommendedBooks[1].isbn, result.body?.books?.get(1)?.isbn)
    }

    @Test
    fun `fetchMoreRecommendations returns a combined list of book recommendations for a user`() {
        val user = testUserEntity()
        val existingRecommendations = listOf(testRecommendedBooksEntity())
        val recommendedBooks = listOf(testBookEntity(), testBookEntity())

        every { userService.getUserById(user.id) } returns user
        every { bookRecommendationRepository.findByUserId(user.id) } returns existingRecommendations
        every { userBooksService.getUserBooks(user.id) } returns listOf(
            testBookEntity(),
            testBookEntity(),
            testBookEntity()
        )
        every { bookService.addBookByIsbn(recommendedBooks[0].isbn) } returns testBookEntity(isbn = recommendedBooks[0].isbn)
        every { bookService.addBookByIsbn(recommendedBooks[1].isbn) } returns testBookEntity(isbn = recommendedBooks[1].isbn)
        every { gptService.isAvailable() } returns true

        every { gptService.getBookRecommendations(any()) } returns
            ResponseEntity.ok(listOf(recommendedBooks[0].isbn, recommendedBooks[1].isbn))

        every { bookRecommendationRepository.save(any()) } returns testRecommendedBooksEntity()

        val result = bookRecommendationService.fetchMoreRecommendations(user.id)
        assertEquals(200, result.statusCode.value())
        assertEquals(3, result.body?.books?.size)
        assertEquals(existingRecommendations[0].book.isbn, result.body?.books?.get(0)?.isbn)
        assertEquals(recommendedBooks[0].isbn, result.body?.books?.get(1)?.isbn)
        assertEquals(recommendedBooks[1].isbn, result.body?.books?.get(2)?.isbn)
    }

    @Test
    fun `fetchMoreRecommendations returns a bad request if user not found`() {
        val userId = testUserEntity().id

        every { userService.getUserById(userId) } returns null
        every { gptService.isAvailable() } returns true

        val result = bookRecommendationService.fetchMoreRecommendations(userId)
        assertEquals(400, result.statusCode.value())
    }

    @Test
    fun `fetchMoreRecommendations returns a bad request if user has insufficient books`() {
        val user = testUserEntity()

        every { userService.getUserById(user.id) } returns user
        every { bookRecommendationRepository.findByUserId(user.id) } returns emptyList()
        every { userBooksService.getUserBooks(user.id) } returns emptyList()
        every { gptService.isAvailable() } returns true

        val result = bookRecommendationService.fetchMoreRecommendations(user.id)
        assertEquals(400, result.statusCode.value())
    }

    @Test
    fun `getRecommendations returns a list of book recommendations for a user`() {
        val user = testUserEntity()
        val existingRecommendations = listOf(testRecommendedBooksEntity())

        every { userService.getUserById(user.id) } returns user
        every { bookRecommendationRepository.findByUserId(user.id) } returns existingRecommendations
        every { gptService.isAvailable() } returns true

        val result = bookRecommendationService.getRecommendations(user.id, fetchMore = false)
        assertEquals(200, result.statusCode.value())
        assertEquals(1, result.body?.books?.size)
        assertEquals(existingRecommendations[0].book.isbn, result.body?.books?.get(0)?.isbn)
    }

    @Test
    fun `getRecommendations returns a bad request if user not found`() {
        val userId = testUserEntity().id

        every { userService.getUserById(userId) } returns null
        every { gptService.isAvailable() } returns true

        val result = bookRecommendationService.getRecommendations(userId, fetchMore = false)

        assertEquals(400, result.statusCode.value())
    }

    @Test
    fun `getRecommendations returns a bad request if user has insufficient books`() {
        val user = testUserEntity()

        every { gptService.isAvailable() } returns true
        every { userService.getUserById(user.id) } returns user
        every { bookRecommendationRepository.findByUserId(user.id) } returns emptyList()
        every { userBooksService.getUserBooks(user.id) } returns emptyList()

        val result = bookRecommendationService.getRecommendations(user.id, fetchMore = false)
        assertEquals(400, result.statusCode.value())
    }

    @Test
    fun `removeRecommendedBookForUser removes a recommended book for a user`() {
        val user = testUserEntity()
        val recommendedBook = testRecommendedBooksEntity()

        every { bookRecommendationRepository.findByUserId(user.id) } returns listOf(recommendedBook)
        every { bookRecommendationRepository.delete(recommendedBook) } returns Unit

        val result = bookRecommendationService.removeRecommendedBookForUser(user.id, recommendedBook.book)
        assertEquals(200, result.statusCode.value())
        assertEquals(recommendedBook.book.isbn, result.body?.isbn)
    }

    @Test
    fun `removeRecommendedBookForUser returns a not found status if book not found in recommendations`() {
        val user = testUserEntity()
        val recommendedBook = testRecommendedBooksEntity()

        every { bookRecommendationRepository.findByUserId(user.id) } returns emptyList()

        val result = bookRecommendationService.removeRecommendedBookForUser(user.id, recommendedBook.book)
        assertEquals(404, result.statusCode.value())
    }
}