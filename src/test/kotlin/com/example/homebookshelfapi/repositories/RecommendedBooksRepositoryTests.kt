package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.entities.UserEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import generateRecommendedBooksEntity

@DataJpaTest
class RecommendedBooksRepositoryTest {

  @Autowired lateinit var userRepository: UserRepository

  @Autowired lateinit var recommendedBooksRepository: RecommendedBooksRepository

  @Autowired lateinit var bookRepository: BookRepository

  lateinit var user: UserEntity

  @BeforeEach
  fun setup() {
    val recommendedBook = generateRecommendedBooksEntity()
    user = userRepository.save(recommendedBook.user)
    val savedBook = bookRepository.save(recommendedBook.book)
    recommendedBooksRepository.save(generateRecommendedBooksEntity(book = savedBook, user = user))
  }

  @AfterEach
  fun tearDown() {
    recommendedBooksRepository.deleteAll()
    bookRepository.deleteAll()
    userRepository.deleteAll()
  }

  @Test
  fun `findByUserId returns recommendations for given user`() {
    val recommendations = recommendedBooksRepository.findByUser(user)
    assertEquals(1, recommendations.size)
    assertEquals(user.id, recommendations.first().user.id)
  }

  @Test
  fun `findByUserId returns empty when no recommendations`() {
    val newUser = userRepository.save(UserEntity(username = "New User", password = "password"))
    val recommendations = recommendedBooksRepository.findByUser(newUser)
    assertTrue(recommendations.isEmpty())
  }

  @Test
  fun `multiple recommendations for the same user`() {
    val anotherBook = bookRepository.save(generateRecommendedBooksEntity().book)
    recommendedBooksRepository.save(generateRecommendedBooksEntity(book = anotherBook, user = user))

    val recommendations = recommendedBooksRepository.findByUser(user)
    assertEquals(2, recommendations.size)
  }
}
