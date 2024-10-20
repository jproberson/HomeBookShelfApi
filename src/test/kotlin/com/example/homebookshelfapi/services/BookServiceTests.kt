package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.external.google.GoogleApiService
import com.example.homebookshelfapi.repositories.BookRepository
import com.example.homebookshelfapi.repositories.UserRepository
import com.example.homebookshelfapi.services.impl.BookServiceImpl
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import java.time.LocalDate
import java.util.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

class BookServiceTest {

    @MockK
    private lateinit var bookRepository: BookRepository

    @MockK
    private lateinit var googleApiService: GoogleApiService

    @MockK
    private lateinit var userBooksService: UserBooksService

    @MockK
    private lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var bookService: BookServiceImpl

    private lateinit var book: BookEntity
    private lateinit var user: UserEntity
    private lateinit var id: UUID

    @BeforeEach
    fun setup() {
        id = UUID.randomUUID()
        MockKAnnotations.init(this)
        book =
            BookEntity(
                id = id,
                isbn = "isbn",
                title = "Sample Book",
                authors = "Author Name",
                description = "Sample description",
                categories = "Fiction",
                publishedDate = LocalDate.of(2022, 1, 1),
                pageCount = 350,
                thumbnail = "some_thumbnail_url"
            )
        user = UserEntity(id = UUID.randomUUID(), username = "Test User", password = "password")
    }

    @Test
    fun getAllBooks_ShouldReturnAllBooks() {
        every { bookRepository.findAll() } returns listOf(book)
        val books = bookService.getAllBooks()
        assertNotNull(books)
        assertEquals(1, books.size)
        assertEquals("Sample Book", books[0].title)
    }

    @Test
    fun getBookById_ShouldReturnBookWhenFound() {
        every { bookRepository.findByIdOrNull(book.id) } returns book
        val foundBook = bookService.getBookById(book.id)
        assertNotNull(foundBook)
        assertEquals("Sample Book", foundBook?.title)
    }

    @Test
    fun addBookToUserByIsbn_ShouldReturnSavedBook_WhenNewBookIsAdded() {
        every { bookRepository.findByIsbn(book.isbn) } returns Optional.empty()
        every { googleApiService.fetchBookInfoByISBN(book.isbn) } returns book
        every { bookRepository.save(book) } returns book
        every { userRepository.findByIdOrNull(user.id) } returns user
        every { userBooksService.addBookToUser(user.username, book.id) } just Runs

        val savedBook = bookService.addBookToUserByIsbn(book.isbn, user.username)

        assertNotNull(savedBook)
        assertEquals("Sample Book", savedBook.title)

        verify { userBooksService.addBookToUser(user.username, savedBook.id) }
    }

    @Test
    fun addBookToUserByIsbn_ShouldReturnBook_WhenBookExists() {
        every { bookRepository.findByIsbn(book.isbn) } returns Optional.of(book)
        every { userRepository.findByIdOrNull(user.id) } returns user
        every { userBooksService.addBookToUser(user.username, book.id) } just Runs

        val existingBook = bookService.addBookToUserByIsbn(book.isbn, user.username)
        assertNotNull(existingBook)
        assertEquals("Sample Book", existingBook.title)

        verify { userBooksService.addBookToUser(user.username, existingBook.id) }
    }

    @Test
    fun addBookByIsbn_ShouldReturnSavedBook_WhenNewBookIsAdded() {
        every { bookRepository.findByIsbn(book.isbn) } returns Optional.empty()
        every { googleApiService.fetchBookInfoByISBN(book.isbn) } returns book
        every { bookRepository.save(book) } returns book

        val savedBook = bookService.addBookByIsbn(book.isbn)

        assertNotNull(savedBook)
        assertEquals("Sample Book", savedBook?.title)
    }

    @Test
    fun addBookByIsbn_ShouldReturnBook_WhenBookExists() {
        every { bookRepository.findByIsbn(book.isbn) } returns Optional.of(book)

        val existingBook = bookService.addBookByIsbn(book.isbn)
        assertNotNull(existingBook)
        assertEquals("Sample Book", existingBook?.title)
    }

    @Test
    fun updateBook_ShouldUpdateBookWhenExists() {
        every { bookRepository.existsById(book.id) } returns true
        every { bookRepository.save(book) } returns book
        val updatedBook = bookService.updateBook(book.id, book)
        assertNotNull(updatedBook)
        assertEquals("Sample Book", updatedBook?.title)
    }

    @Test
    fun deleteBook_ShouldReturnDeleteBookWhenBookForUserExistsForUser() {
        every { userBooksService.getUserBooks(user.username) } returns listOf(book)
        every { userBooksService.deleteBookForUser(user.username, book.id) } returns true

        val deleteBook = bookService.deleteBookForUser(book.id, user.username)

        assertEquals(deleteBook, book)
        verify { userBooksService.deleteBookForUser(user.username, book.id) }
    }

    @Test
    fun deleteBook_ShouldReturnNullWhenBookForUserNotFound() {
        every { userBooksService.getUserBooks(user.username) } returns emptyList()

        val deleteBook = bookService.deleteBookForUser(book.id, user.username)
        assertNull(deleteBook)
    }
}
