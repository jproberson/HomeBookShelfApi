package com.example.homebookshelfapi.services.impl

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.domain.entities.UserBooksEntity
import com.example.homebookshelfapi.repositories.BookRepository
import com.example.homebookshelfapi.repositories.UserBooksRepository
import com.example.homebookshelfapi.services.UserBooksService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserBooksServiceImpl(
    private val userBookRepository: UserBooksRepository,
    private val bookRepository: BookRepository
) : UserBooksService {

    override fun getUserBooks(userId: UUID): List<BookEntity> {
        val userBooks = userBookRepository.findByUserId(userId)
        return userBooks.map { userBook ->
            bookRepository.findById(userBook.bookId).orElseThrow {
                IllegalStateException("Book not found for id: ${userBook.bookId}")
            }
        }
    }

    override fun addBookToUser(userId: UUID, bookId: UUID) {
        if (!userBookRepository.existsByUserIdAndBookId(userId, bookId)) {
            val userBook = UserBooksEntity(userId = userId, bookId = bookId)
            userBookRepository.save(userBook)
        }
    }

    @Transactional
    override fun deleteBookForUser(userId: UUID, bookId: UUID): Boolean {
        return if (userBookRepository.existsByUserIdAndBookId(userId, bookId)) {
            userBookRepository.deleteByUserIdAndBookId(userId, bookId)
            true
        } else {
            false
        }
    }

}