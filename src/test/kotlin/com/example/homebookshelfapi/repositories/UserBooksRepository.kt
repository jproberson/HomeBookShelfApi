package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.domain.entities.UserBooksEntity
import com.example.homebookshelfapi.domain.entities.UserEntity
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class UserEntityBooksRepositoryTest {

  @Autowired private lateinit var userRepository: UserRepository

  @Autowired private lateinit var bookRepository: BookRepository

  @Autowired private lateinit var userBooksRepository: UserBooksRepository

  private lateinit var user: UserEntity
  private lateinit var book: BookEntity
  private lateinit var userBook: UserBooksEntity

  @BeforeEach
  fun setup() {
    user = userRepository.save(UserEntity(username = "Test User", password = "password"))
    book =
      bookRepository.save(
        BookEntity(
          isbn = "1234567890",
          title = "Sample Book",
          authors = "Sample Author",
          description = "Sample Description",
          categories = "Fiction",
          publishedDate = LocalDate.of(2022, 1, 1),
          pageCount = 350,
          thumbnail = "some_thumbnail_url"
        )
      )
    userBook = UserBooksEntity(user = user, book = book)
    userBooksRepository.save(userBook)
  }

  @Test
  fun `existsByUserAndBookId should return true when a UserBook exists`() {
    val exists = userBooksRepository.existsByUserAndBookId(user, book.id)
    assertTrue(exists)
  }

  @Test
  fun `existsByUserAndBookId should return false when a UserBook does not exist`() {
    val otherBookEntity =
      bookRepository.save(
        BookEntity(
          isbn = "0987654321",
          title = "Another Book",
          authors = "Another Author",
          description = "Another Description",
          categories = "Non-fiction",
          publishedDate = LocalDate.of(2021, 1, 1),
          pageCount = 250,
          thumbnail = "other_thumbnail_url"
        )
      )
    val exists = userBooksRepository.existsByUserAndBookId(user, otherBookEntity.id)
    assertFalse(exists)
  }

  @Test
  fun `deleteByUserAndBookId should delete UserBook`() {
    userBooksRepository.deleteByUserAndBookId(user, book.id)
    val exists = userBooksRepository.existsByUserAndBookId(user, book.id)
    assertFalse(exists)
  }

  @Test
  fun `findByUserId should return UserBooks for a specific user`() {
    val userBooks = userBooksRepository.findBooksByUserId(user.id)
    assertNotNull(userBooks)
    assertEquals(1, userBooks.size)
    assertEquals(book.id, userBooks[0].id)
  }
}
