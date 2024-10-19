package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.entities.BookEntity
import jakarta.transaction.Transactional
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
@Transactional
class BookEntityRepositoryTest {

  @Autowired private lateinit var bookRepository: BookRepository

  private lateinit var book: BookEntity

  @BeforeEach
  fun setup() {
    book =
      BookEntity(
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
    val foundBook = bookRepository.findByIdOrNull(savedBook.id)
    assertTrue(foundBook != null)
    assertEquals(savedBook.title, foundBook?.title)
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
    val foundBook = bookRepository.findByIdOrNull(savedBook.id)
    assertFalse(foundBook != null)
  }
}
