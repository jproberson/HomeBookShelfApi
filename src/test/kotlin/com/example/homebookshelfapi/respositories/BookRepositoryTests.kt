package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.Book
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDate

@DataJpaTest
@Transactional
class BookRepositoryTest {

    @Autowired
    private lateinit var bookRepository: BookRepository

    private lateinit var book: Book

    @BeforeEach
    fun setup() {
        book = Book(
            isbn = "1234567890",
            title = "Sample Book",
            authors = "Author Name",
            description = "Sample description",
            categories = "Sample category",
            publishedDate = LocalDate.of(2022, 1, 1),
            pageCount = 300,
            thumbnail = "some_thumbnail_url"
        )
    }

    @Test
    fun saveBook_ShouldSaveAndReturnBook() {
        val savedBook = bookRepository.save(book)
        assertNotNull(savedBook.id)
        assertEquals("Sample Book", savedBook.title)
    }

    @Test
    fun findBookById_ShouldReturnBookWhenFound() {
        val savedBook = bookRepository.save(book)
        val foundBook = bookRepository.findById(savedBook.id)
        assertTrue(foundBook.isPresent)
        assertEquals(savedBook.title, foundBook.get().title)
    }

    @Test
    fun findBookByIsbn_ShouldReturnBookWhenFound() {
        val savedBook = bookRepository.save(book)
        val foundBook = bookRepository.findByIsbn(savedBook.isbn)
        assertTrue(foundBook.isPresent)
        assertEquals(savedBook.title, foundBook.get().title)
    }

    @Test
    fun findAllBooks_ShouldReturnAllBooks() {
        bookRepository.save(book)
        val books = bookRepository.findAll()
        assertTrue(books.isNotEmpty())
    }

    @Test
    fun deleteBook_ShouldRemoveBookById() {
        val savedBook = bookRepository.save(book)
        bookRepository.deleteById(savedBook.id)
        val foundBook = bookRepository.findById(savedBook.id)
        assertFalse(foundBook.isPresent)
    }
}
