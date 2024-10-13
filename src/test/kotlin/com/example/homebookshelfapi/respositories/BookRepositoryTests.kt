package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.models.Book
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.*

@DataJpaTest
@Transactional
class BookRepositoryTest {

    // TODO: Switch to using Test Containers?

    @Autowired
    private lateinit var bookRepository: BookRepository

    private lateinit var book: Book

    @BeforeEach
    fun setup() {
        book = Book(isbn = UUID.randomUUID().toString(), title = "Sample Book", author = "Author Name", publishedYear = 2022)
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
