package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.domain.entities.UserBooksEntity
import com.example.homebookshelfapi.repositories.BookRepository
import com.example.homebookshelfapi.repositories.UserBooksRepository
import com.example.homebookshelfapi.services.impl.UserBooksServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserEntityBooksServiceTest {

    @Mock
    private lateinit var bookRepository: BookRepository

    @Mock
    private lateinit var userBooksRepository: UserBooksRepository

    @InjectMocks
    private lateinit var userBooksService: UserBooksServiceImpl

    private lateinit var user: UserEntity
    private lateinit var book: BookEntity
    private lateinit var userBook: UserBooksEntity

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
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
    fun `getUserBooks should return books for a specific user`() {
        `when`(userBooksRepository.findByUserId(user.id)).thenReturn(listOf(userBook))
        `when`(bookRepository.findById(book.id)).thenReturn(Optional.of(book))

        val userBooks = userBooksService.getUserBooks(user.id)

        assertNotNull(userBooks)
        assertEquals(1, userBooks.size)
        assertEquals("Sample Book", userBooks[0].title)
    }


    @Test
    fun `addBookToUser should save a new UserBook when it does not exist`() {
        `when`(userBooksRepository.existsByUserIdAndBookId(user.id, book.id)).thenReturn(false)
        userBooksService.addBookToUser(user.id, book.id)
        verify(userBooksRepository, times(1)).save(any(UserBooksEntity::class.java))
    }

    @Test
    fun `addBookToUser should not save a UserBook when it already exists`() {
        `when`(userBooksRepository.existsByUserIdAndBookId(user.id, book.id)).thenReturn(true)
        userBooksService.addBookToUser(user.id, book.id)
        verify(userBooksRepository, never()).save(any(UserBooksEntity::class.java))
    }

    @Test
    fun `deleteBookForUser should return true and delete UserBook if it exists`() {
        `when`(userBooksRepository.existsByUserIdAndBookId(user.id, book.id)).thenReturn(true)
        val result = userBooksService.deleteBookForUser(user.id, book.id)
        assertTrue(result)
        verify(userBooksRepository, times(1)).deleteByUserIdAndBookId(user.id, book.id)
    }

    @Test
    fun `deleteBookForUser should return false if UserBook does not exist`() {
        `when`(userBooksRepository.existsByUserIdAndBookId(user.id, book.id)).thenReturn(false)
        val result = userBooksService.deleteBookForUser(user.id, book.id)
        assertFalse(result)
        verify(userBooksRepository, never()).deleteByUserIdAndBookId(user.id, book.id)
    }
}
