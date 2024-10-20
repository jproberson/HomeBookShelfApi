package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.domain.entities.UserBooksEntity
import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.exceptions.UserNotFoundException
import com.example.homebookshelfapi.repositories.BookRepository
import com.example.homebookshelfapi.repositories.UserBooksRepository
import com.example.homebookshelfapi.repositories.UserRepository
import com.example.homebookshelfapi.services.impl.UserBooksServiceImpl
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import generateBookEntity

class UserEntityBooksServiceTest {

  @MockK private lateinit var bookRepository: BookRepository

  @MockK private lateinit var userBooksRepository: UserBooksRepository

  @MockK private lateinit var userRepository: UserRepository

  @InjectMockKs private lateinit var userBooksService: UserBooksServiceImpl

  private lateinit var user: UserEntity
  private lateinit var book: BookEntity
  private lateinit var userBook: UserBooksEntity

  @BeforeEach
  fun setup() {
    MockKAnnotations.init(this)
    user = UserEntity(id = UUID.randomUUID(), username = "Test User", password = "password")
    book = generateBookEntity()
    userBook = UserBooksEntity(user = user, book = book)
  }

  @Test
  fun `getUserBooks should return list of books for a user`() {
    every { userRepository.findByUsername(user.username) } returns user
    every { userBooksRepository.findBooksByUserId(user.id) } returns listOf(book)
    val userBooks = userBooksService.getUserBooks(user.username)

    assertNotNull(userBooks)
    assertTrue(userBooks.isNotEmpty())
    assertEquals(book.title, userBooks[0].title)
    verify { userBooksRepository.findBooksByUserId(user.id) }
  }

  @Test
  fun `getUserBooks should empty list when a book is not found`() {
    every { userRepository.findByUsername(user.username) } returns user
    every { userBooksRepository.findBooksByUserId(user.id) } returns emptyList()

    val userBooks = userBooksService.getUserBooks(user.username)
    assertTrue(userBooks.isEmpty())
  }

  @Test
  fun `getUserBooks should throw IllegalArgumentException when user is not found`() {
    every { userRepository.findByUsername(user.username) } returns null

    val exception =
      org.junit.jupiter.api.assertThrows<UserNotFoundException> {
        userBooksService.getUserBooks(user.username)
      }

    assertEquals("User with username ${user.username} not found.", exception.message)
  }

  @Test
  fun `deleteBookForUser should return true and delete UserBook if it exists`() {
    every { userRepository.findByUsername(user.username) } returns user
    every { userBooksRepository.existsByUserAndBookId(user, book.id) } returns true
    every { userBooksRepository.deleteByUserAndBookId(user, book.id) } just Runs

    val result = userBooksService.deleteBookForUser(user.username, book.id)

    assertTrue(result)
    verify { userBooksRepository.deleteByUserAndBookId(user, book.id) }
  }

  @Test
  fun `deleteBookForUser should return false if UserBook does not exist`() {
    every { userRepository.findByUsername(user.username) } returns user
    every { userBooksRepository.existsByUserAndBookId(user, book.id) } returns false

    val result = userBooksService.deleteBookForUser(user.username, book.id)

    assertFalse(result)
    verify(exactly = 0) { userBooksRepository.deleteByUserAndBookId(user, book.id) }
  }

  @Test
  fun `deleteBookForUser should throw IllegalArgumentException when user is not found`() {
    every { userRepository.findByUsername(user.username) } returns null

    val exception =
      org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
        userBooksService.deleteBookForUser(user.username, book.id)
      }

    assertEquals("User with username ${user.username} not found.", exception.message)
  }

  @Test
  fun `addBookToUser should save a new UserBook when it does not exist`() {
    every { userRepository.findByUsername(user.username) } returns user
    every { userRepository.findById(user.id) } returns Optional.of(user)
    every { bookRepository.findById(book.id) } returns Optional.of(book)
    every { userBooksRepository.existsByUserAndBookId(user, book.id) } returns false
    every { userBooksRepository.save(any<UserBooksEntity>()) } returns userBook

    userBooksService.addBookToUser(user.username, book.id)

    verify { userBooksRepository.save(any<UserBooksEntity>()) }
  }

  @Test
  fun `addBookToUser should not save a UserBook when it already exists`() {
    every { userRepository.findByUsername(user.username) } returns user
    every { userBooksRepository.existsByUserAndBookId(user, book.id) } returns true

    userBooksService.addBookToUser(user.username, book.id)

    verify(exactly = 0) { userBooksRepository.save(any<UserBooksEntity>()) }
  }

  @Test
  fun `addBookToUser should throw IllegalArgumentException when user is not found`() {
    every { userRepository.findByUsername(user.username) } returns null

    val exception =
      org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
        userBooksService.addBookToUser(user.username, book.id)
      }

    assertEquals("User with username ${user.username} not found.", exception.message)
  }
}
