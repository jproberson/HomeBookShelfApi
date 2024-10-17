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

        every { gptService.getBookRecommendations(any()) } returns
            ResponseEntity.ok(listOf(recommendedBooks[0].isbn, recommendedBooks[1].isbn))

        every { bookRecommendationRepository.save(any()) } returns testRecommendedBooksEntity()


        val result = bookRecommendationService.fetchMoreRecommendations(user.id)
        assertEquals(200, result.statusCode.value())
        assertEquals(2, result.body?.size)
        assertEquals(recommendedBooks[0].isbn, result.body?.get(0)?.isbn)
        assertEquals(recommendedBooks[1].isbn, result.body?.get(1)?.isbn)
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

        every { gptService.getBookRecommendations(any()) } returns
            ResponseEntity.ok(listOf(recommendedBooks[0].isbn, recommendedBooks[1].isbn))

        every { bookRecommendationRepository.save(any()) } returns testRecommendedBooksEntity()

        val result = bookRecommendationService.fetchMoreRecommendations(user.id)
        assertEquals(200, result.statusCode.value())
        assertEquals(3, result.body?.size)
        assertEquals(existingRecommendations[0].book.isbn, result.body?.get(0)?.isbn)
        assertEquals(recommendedBooks[0].isbn, result.body?.get(1)?.isbn)
        assertEquals(recommendedBooks[1].isbn, result.body?.get(2)?.isbn)
    }

    @Test
    fun `fetchMoreRecommendations returns a bad request if user not found`() {
        val userId = testUserEntity().id

        every { userService.getUserById(userId) } returns null

        val result = bookRecommendationService.fetchMoreRecommendations(userId)
        assertEquals(400, result.statusCode.value())
    }

    @Test
    fun `fetchMoreRecommendations returns a bad request if user has insufficient books`() {
        val user = testUserEntity()

        every { userService.getUserById(user.id) } returns user
        every { bookRecommendationRepository.findByUserId(user.id) } returns emptyList()
        every { userBooksService.getUserBooks(user.id) } returns emptyList()

        val result = bookRecommendationService.fetchMoreRecommendations(user.id)
        assertEquals(400, result.statusCode.value())
    }

    @Test
    fun `getRecommendations returns a list of book recommendations for a user`() {
        val user = testUserEntity()
        val existingRecommendations = listOf(testRecommendedBooksEntity())

        every { userService.getUserById(user.id) } returns user
        every { bookRecommendationRepository.findByUserId(user.id) } returns existingRecommendations

        val result = bookRecommendationService.getRecommendations(user.id, fetchMore = false)
        assertEquals(200, result.statusCode.value())
        assertEquals(1, result.body?.size)
        assertEquals(existingRecommendations[0].book.isbn, result.body?.get(0)?.isbn)
    }

    @Test
    fun `getRecommendations returns a bad request if user not found`() {
        val userId = testUserEntity().id

        every { userService.getUserById(userId) } returns null

        val result = bookRecommendationService.getRecommendations(userId, fetchMore = false)

        assertEquals(400, result.statusCode.value())
    }

    @Test
    fun `getRecommendations returns a bad request if user has insufficient books`() {
        val user = testUserEntity()

        every { userService.getUserById(user.id) } returns user
        every { bookRecommendationRepository.findByUserId(user.id) } returns emptyList()
        every { userBooksService.getUserBooks(user.id) } returns emptyList()

        val result = bookRecommendationService.getRecommendations(user.id, fetchMore = false)
        assertEquals(400, result.statusCode.value())
    }
}