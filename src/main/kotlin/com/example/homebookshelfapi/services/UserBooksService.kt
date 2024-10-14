package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.Book
import com.example.homebookshelfapi.domain.UserBooks
import com.example.homebookshelfapi.repositories.BookRepository
import com.example.homebookshelfapi.repositories.UserBooksRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserBooksService(
    private val userBookRepository: UserBooksRepository,
    private val bookRepository: BookRepository
) {

    fun getUserBooks(userId: UUID): List<Book> {
        val userBooks = userBookRepository.findByUserId(userId)
        return userBooks.map { userBook ->
            bookRepository.findById(userBook.bookId).orElseThrow {
                IllegalStateException("Book not found for id: ${userBook.bookId}")
            }
        }
    }

    fun addBookToUser(userId: UUID, bookId: UUID) {
        if (!userBookRepository.existsByUserIdAndBookId(userId, bookId)) {
            val userBook = UserBooks(userId = userId, bookId = bookId)
            userBookRepository.save(userBook)
        }
    }

    @Transactional
    fun deleteBookForUser(userId: UUID, bookId: UUID): Boolean {
        return if (userBookRepository.existsByUserIdAndBookId(userId, bookId)) {
            userBookRepository.deleteByUserIdAndBookId(userId, bookId)
            true
        } else {
            false
        }
    }
}
