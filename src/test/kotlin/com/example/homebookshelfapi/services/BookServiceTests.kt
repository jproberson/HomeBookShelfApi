package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.external.google.GoogleApiService
import com.example.homebookshelfapi.domain.Book
import com.example.homebookshelfapi.repositories.BookRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.time.LocalDate
import java.util.*

class BookServiceTest {

    @Mock
    private lateinit var bookRepository: BookRepository

    @Mock
    private lateinit var googleApiService: GoogleApiService

    @InjectMocks
    private lateinit var bookService: BookService

    private lateinit var book: Book
    private lateinit var id: UUID

    @BeforeEach
    fun setup() {
        id = UUID.randomUUID()
        MockitoAnnotations.openMocks(this)
        book = Book(
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
    }

    @Test
    fun getAllBooks_ShouldReturnAllBooks() {
        `when`(bookRepository.findAll()).thenReturn(listOf(book))
        val books = bookService.getAllBooks()
        assertNotNull(books)
        assertEquals(1, books.size)
        assertEquals("Sample Book", books[0].title)
    }

    @Test
    fun getBookById_ShouldReturnBookWhenFound() {
        `when`(bookRepository.findById(book.id)).thenReturn(Optional.of(book))
        val foundBook = bookService.getBookById(book.id)
        assertNotNull(foundBook)
        assertEquals("Sample Book", foundBook?.title)
    }

    @Test
    fun addBookByIsbn_ShouldReturnSavedBook() {
        `when`(bookRepository.findByIsbn(book.isbn)).thenReturn(Optional.empty())
        `when`(googleApiService.fetchBookInfoByISBN(book.isbn)).thenReturn(book)
        `when`(bookRepository.save(book)).thenReturn(book)
        val savedBook = bookService.addBookByIsbn(book.isbn)
        assertNotNull(savedBook)
        assertEquals("Sample Book", savedBook.title)
    }

    @Test
    fun addBookByIsbn_ShouldReturnException_WhenBookExists() {
        `when`(bookRepository.findByIsbn(book.isbn)).thenReturn(Optional.of(book))
        assertThrows(IllegalArgumentException::class.java) {
            bookService.addBookByIsbn(book.isbn)
        }
    }

    @Test
    fun addBook_ShouldReturnSavedBook() {
        `when`(bookRepository.save(book)).thenReturn(book)
        val savedBook = bookService.addBook(book)
        assertNotNull(savedBook)
        assertEquals("Sample Book", savedBook.title)
    }

    @Test
    fun addBook_ShouldReturnException_WhenBookExists() {
        `when`(bookRepository.findByIsbn(book.isbn)).thenReturn(Optional.of(book))
        assertThrows(IllegalArgumentException::class.java) {
            bookService.addBook(book)
        }
    }

    @Test
    fun updateBook_ShouldUpdateBookWhenExists() {
        `when`(bookRepository.existsById(book.id)).thenReturn(true)
        `when`(bookRepository.save(book)).thenReturn(book)
        val updatedBook = bookService.updateBook(book.id, book)
        assertNotNull(updatedBook)
        assertEquals("Sample Book", updatedBook?.title)
    }

    @Test
    fun deleteBook_ShouldReturnTrueWhenBookExists() {
        `when`(bookRepository.existsById(book.id)).thenReturn(true)
        doNothing().`when`(bookRepository).deleteById(book.id)
        val isDeleted = bookService.deleteBook(book.id)
        assertTrue(isDeleted)
    }

    @Test
    fun deleteBook_ShouldReturnFalseWhenBookNotFound() {
        `when`(bookRepository.existsById(book.id)).thenReturn(false)
        val isDeleted = bookService.deleteBook(book.id)
        assertFalse(isDeleted)
    }
}
