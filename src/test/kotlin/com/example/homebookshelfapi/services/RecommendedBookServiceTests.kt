package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.exceptions.UserNotFoundException
import com.example.homebookshelfapi.external.gpt.GptService
import com.example.homebookshelfapi.repositories.RecommendedBooksRepository
import com.example.homebookshelfapi.services.impl.RecommendedRecommendedBookServiceImpl
import generateBookEntity
import generateRecommendedBooksEntity
import generateUserEntity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.ResponseEntity

class RecommendedBookServiceTests {
    @MockK
    private lateinit var userBooksService: UserBooksService

    @MockK
    private lateinit var userService: UsersService

    @MockK
    private lateinit var gptService: GptService

    @MockK
    private lateinit var bookService: BookService

    @MockK
    private lateinit var recommendedBooksRepository: RecommendedBooksRepository

    @InjectMockKs
    private lateinit var bookRecommendationService: RecommendedRecommendedBookServiceImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getRecommendationsForUser returns a list of book recommendations for a user`() {
        val user = generateUserEntity()
        val existingRecommendations = listOf(generateRecommendedBooksEntity())

        every { userService.getByUsername(user.username) } returns user
        every { recommendedBooksRepository.findByUser(user) } returns existingRecommendations
        every { gptService.isAvailable() } returns true

        val recommendations =
            bookRecommendationService.getRecommendationsForUser(user.username, fetchMore = false)
        assertEquals(1, recommendations.books.size)
        assertEquals(existingRecommendations[0].book.isbn, recommendations.books[0].isbn)
    }

    @Test
    fun `getRecommendationsForUser returns a UserNotFoundException exception if user not found`() {
        val user = generateUserEntity()

        every { userService.getByUsername(user.username) } returns null
        every { gptService.isAvailable() } returns true

        assertThrows<UserNotFoundException> {
            bookRecommendationService.getRecommendationsForUser(
                user.username,
                fetchMore = false
            )
        }
    }

    @Test
    fun `getRecommendationsForUser returns an empty list if user has insufficient books`() {
        val user = generateUserEntity()

        every { gptService.isAvailable() } returns true
        every { userService.getByUsername(user.username) } returns user
        every { recommendedBooksRepository.findByUser(user) } returns emptyList()
        every { userBooksService.getUserBooks(user.username) } returns emptyList()

        val recommendations =
            bookRecommendationService.getRecommendationsForUser(user.username, fetchMore = false)

        assertEquals(0, recommendations.books.size)
    }

    @Test
    fun `removeRecommendedBookForUser removes a recommended book for a user`() {
        val user = generateUserEntity()
        val recommendedBook = generateRecommendedBooksEntity()

        every { userService.getByUsername(user.username) } returns user
        every { recommendedBooksRepository.findByUser(user) } returns listOf(recommendedBook)
        every { recommendedBooksRepository.delete(recommendedBook) } returns Unit

        val removedBook =
            bookRecommendationService.removeRecommendedBookForUser(user.username, recommendedBook.book)

        assertEquals(recommendedBook.book.isbn, removedBook?.isbn)
    }

    @Test
    fun `removeRecommendedBookForUser returns null if book not found in recommendations`() {
        val user = generateUserEntity()
        val recommendedBook = generateRecommendedBooksEntity()

        every { userService.getByUsername(user.username) } returns user
        every { recommendedBooksRepository.findByUser(user) } returns emptyList()

        val removedBook =
            bookRecommendationService.removeRecommendedBookForUser(user.username, recommendedBook.book)
        assertNull(removedBook)
    }

    @Test
    fun `getRecommendationsForUser fetches and saves new recommendations if existing is empty`() {
        val user = generateUserEntity()
        val userBooks = listOf(generateBookEntity(), generateBookEntity(), generateBookEntity())
        val newRecommendations = listOf(generateBookEntity())

        every { userService.getByUsername(user.username) } returns user
        every { recommendedBooksRepository.findByUser(user) } returns emptyList()
        every { userBooksService.getUserBooks(user.username) } returns userBooks
        every { gptService.getBookRecommendations(any()) } returns
                ResponseEntity.ok(listOf(newRecommendations[0].isbn))
        every { bookService.addBookByIsbn(any()) } returns newRecommendations[0]
        every { recommendedBooksRepository.save(any()) } returns generateRecommendedBooksEntity()
        every { gptService.isAvailable() } returns true

        val recommendations =
            bookRecommendationService.getRecommendationsForUser(user.username, fetchMore = false)
        assertEquals(1, recommendations.books.size)
    }

    @Test
    fun `getRecommendationsForUser fetches more recommendations when fetchMore is true`() {
        val user = generateUserEntity()
        val existingRecommendations = listOf(generateRecommendedBooksEntity())
        val newRecommendations = listOf(generateBookEntity(), generateBookEntity())

        every { userService.getByUsername(user.username) } returns user
        every { recommendedBooksRepository.findByUser(user) } returns existingRecommendations
        every { userBooksService.getUserBooks(user.username) } returns
                listOf(generateBookEntity(), generateBookEntity(), generateBookEntity())
        every { gptService.getBookRecommendations(any()) } returns
                ResponseEntity.ok(listOf(newRecommendations[0].isbn))
        every { bookService.addBookByIsbn(any()) } returns newRecommendations[0]
        every { recommendedBooksRepository.save(any()) } returns generateRecommendedBooksEntity()
        every { gptService.isAvailable() } returns true

        val recommendedBooks =
            bookRecommendationService.getRecommendationsForUser(user.username, fetchMore = true)
        assertEquals(2, recommendedBooks.books.size)
    }

    @Test
    fun `getRecommendationsForUser saves new recommendations when fetchMore is true`() {
        val user = generateUserEntity()
        val userBooks = listOf(generateBookEntity(), generateBookEntity(), generateBookEntity())
        val newRecommendations = listOf(generateBookEntity())

        every { userService.getByUsername(user.username) } returns user
        every { recommendedBooksRepository.findByUser(user) } returns emptyList()
        every { userBooksService.getUserBooks(user.username) } returns userBooks
        every { gptService.getBookRecommendations(any()) } returns
                ResponseEntity.ok(listOf(newRecommendations[0].isbn))
        every { bookService.addBookByIsbn(newRecommendations[0].isbn) } returns newRecommendations[0]
        every { recommendedBooksRepository.save(any()) } returns generateRecommendedBooksEntity()
        every { gptService.isAvailable() } returns true

        val recommendations =
            bookRecommendationService.getRecommendationsForUser(user.username, fetchMore = true)
        assertEquals(1, recommendations.books.size)
        verify { bookService.addBookByIsbn(newRecommendations[0].isbn) }
    }

    @Test
    fun `getRecommendationsForUser does not fetch new recommendations when fetchMore is false and recommendations exist`() {
        val user = generateUserEntity()
        val existingRecommendations = listOf(generateRecommendedBooksEntity())

        every { userService.getByUsername(user.username) } returns user
        every { recommendedBooksRepository.findByUser(user) } returns existingRecommendations
        every { gptService.isAvailable() } returns true

        val result =
            bookRecommendationService.getRecommendationsForUser(user.username, fetchMore = false)
        assertEquals(1, result.books.size)
        verify(exactly = 0) { gptService.getBookRecommendations(any()) }
    }
}
