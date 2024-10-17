package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.entities.UserEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import testRecommendedBooksEntity

@DataJpaTest
class BookRecommendationRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var bookRecommendationRepository: BookRecommendationRepository

    @Autowired
    lateinit var bookRepository: BookRepository

    lateinit var user: UserEntity

    @BeforeEach
    fun setup() {
        val recommendedBook = testRecommendedBooksEntity()
        user = userRepository.save(recommendedBook.user)
        val savedBook = bookRepository.save(recommendedBook.book)
        bookRecommendationRepository.save(testRecommendedBooksEntity(book = savedBook, user = user))
    }

    @AfterEach
    fun tearDown() {
        bookRecommendationRepository.deleteAll()
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `findByUserId returns recommendations for given user`() {
        val recommendations = bookRecommendationRepository.findByUserId(user.id)
        assertEquals(1, recommendations.size)
        assertEquals(user.id, recommendations.first().user.id)
    }

    @Test
    fun `findByUserId returns empty when no recommendations`() {
        val newUser = userRepository.save(UserEntity(name = "New User"))
        val recommendations = bookRecommendationRepository.findByUserId(newUser.id)
        assertTrue(recommendations.isEmpty())
    }

    @Test
    fun `multiple recommendations for the same user`() {
        val anotherBook =
            bookRepository.save(testRecommendedBooksEntity().book)
        bookRecommendationRepository.save(testRecommendedBooksEntity(book = anotherBook, user = user))

        val recommendations = bookRecommendationRepository.findByUserId(user.id)
        assertEquals(2, recommendations.size)
    }
}
