package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.domain.entities.UserBooksEntity
import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.repositories.BookRepository
import com.example.homebookshelfapi.repositories.UserBooksRepository
import com.example.homebookshelfapi.services.impl.UserBooksServiceImpl
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserEntityBooksServiceTest {

    @MockK
    private lateinit var bookRepository: BookRepository

    @MockK
    private lateinit var userBooksRepository: UserBooksRepository

    @InjectMockKs
    private lateinit var userBooksService: UserBooksServiceImpl

    private lateinit var user: UserEntity
    private lateinit var book: BookEntity
    private lateinit var userBook: UserBooksEntity

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        user = UserEntity(id = UUID.randomUUID(), name = "Test User")
        book = BookEntity(
            id = UUID.randomUUID(),
            isbn = "1234567890",
            title = "Sample Book",
            authors = "Author",
            description = "Sample Description",
            categories = "Fiction",
            publishedDate = LocalDate.of(2020, 1, 1),
            pageCount = 100,
            thumbnail = "some_thumbnail_url"
        )
        userBook = UserBooksEntity(userId = user.id, bookId = book.id)
    }

    @Test
    fun `getUserBooks should return list of books for a user`() {
        every { userBooksRepository.findByUserId(user.id) } returns listOf(userBook)
        every { bookRepository.findById(userBook.bookId) } returns Optional.of(book)

        val userBooks = userBooksService.getUserBooks(user.id)

        assertNotNull(userBooks)
        assertTrue(userBooks.isNotEmpty())
        assertEquals("Sample Book", userBooks[0].title)
        verify { userBooksRepository.findByUserId(user.id) }
        verify { bookRepository.findById(userBook.bookId) }
    }

    @Test
    fun `getUserBooks should throw exception when a book is not found`() {
        every { userBooksRepository.findByUserId(user.id) } returns listOf(userBook)
        every { bookRepository.findById(userBook.bookId) } returns Optional.empty()

        val exception = assertThrows<IllegalStateException> {
            userBooksService.getUserBooks(user.id)
        }

        assertEquals("Book not found for id: ${userBook.bookId}", exception.message)
        verify { userBooksRepository.findByUserId(user.id) }
        verify { bookRepository.findById(userBook.bookId) }
    }


    @Test
    fun `deleteBookForUser should return true and delete UserBook if it exists`() {
        every { userBooksRepository.existsByUserIdAndBookId(user.id, book.id) } returns true
        every { userBooksRepository.deleteByUserIdAndBookId(user.id, book.id) } just Runs

        val result = userBooksService.deleteBookForUser(user.id, book.id)

        assertTrue(result)
        verify { userBooksRepository.deleteByUserIdAndBookId(user.id, book.id) }
    }

    @Test
    fun `deleteBookForUser should return false if UserBook does not exist`() {
        every { userBooksRepository.existsByUserIdAndBookId(user.id, book.id) } returns false

        val result = userBooksService.deleteBookForUser(user.id, book.id)

        assertFalse(result)
        verify(exactly = 0) { userBooksRepository.deleteByUserIdAndBookId(user.id, book.id) }
    }

    @Test
    fun `addBookToUser should save a new UserBook when it does not exist`() {
        every { userBooksRepository.existsByUserIdAndBookId(user.id, book.id) } returns false
        every { userBooksRepository.save(any<UserBooksEntity>()) } returns userBook // Mock the save behavior

        userBooksService.addBookToUser(user.id, book.id)

        verify { userBooksRepository.save(any<UserBooksEntity>()) }
    }

    @Test
    fun `addBookToUser should not save a UserBook when it already exists`() {
        every { userBooksRepository.existsByUserIdAndBookId(user.id, book.id) } returns true

        userBooksService.addBookToUser(user.id, book.id)

        verify(exactly = 0) { userBooksRepository.save(any<UserBooksEntity>()) }
    }
}
