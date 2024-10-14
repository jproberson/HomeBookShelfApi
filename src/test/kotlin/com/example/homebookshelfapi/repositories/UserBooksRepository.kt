package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.Book
import com.example.homebookshelfapi.domain.User
import com.example.homebookshelfapi.domain.UserBooks
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDate

@DataJpaTest
class UserBooksRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var userBooksRepository: UserBooksRepository

    private lateinit var user: User
    private lateinit var book: Book
    private lateinit var userBook: UserBooks

    @BeforeEach
    fun setup() {
        user = userRepository.save(User(name = "Test User"))
        book = bookRepository.save(
            Book(
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
        userBook = UserBooks(userId = user.id, bookId = book.id)
        userBooksRepository.save(userBook)
    }

    @Test
    fun `existsByUserIdAndBookId should return true when a UserBook exists`() {
        val exists = userBooksRepository.existsByUserIdAndBookId(user.id, book.id)
        assertTrue(exists)
    }

    @Test
    fun `existsByUserIdAndBookId should return false when a UserBook does not exist`() {
        val otherBook = bookRepository.save(
            Book(
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
        val exists = userBooksRepository.existsByUserIdAndBookId(user.id, otherBook.id)
        assertFalse(exists)
    }

    @Test
    fun `deleteByUserIdAndBookId should delete UserBook`() {
        userBooksRepository.deleteByUserIdAndBookId(user.id, book.id)
        val exists = userBooksRepository.existsByUserIdAndBookId(user.id, book.id)
        assertFalse(exists)
    }

    @Test
    fun `findByUserId should return UserBooks for a specific user`() {
        val userBooks = userBooksRepository.findByUserId(user.id)
        assertNotNull(userBooks)
        assertEquals(1, userBooks.size)
        assertEquals(book.id, userBooks[0].bookId)
    }
}
