package com.example.homebookshelfapi.services.impl

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.domain.entities.UserBooksEntity
import com.example.homebookshelfapi.repositories.BookRepository
import com.example.homebookshelfapi.repositories.UserBooksRepository
import com.example.homebookshelfapi.repositories.UserRepository
import com.example.homebookshelfapi.services.UserBooksService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserBooksServiceImpl(
    private val userBookRepository: UserBooksRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) : UserBooksService {

    override fun getUserBooks(userId: UUID): List<BookEntity> {
        return userBookRepository.findBooksByUserId(userId)
    }

    override fun addBookToUser(userId: UUID, bookId: UUID) {
        if (!userBookRepository.existsByUserIdAndBookId(userId, bookId)) {
            val user = userRepository.findById(userId).orElseThrow {
                IllegalArgumentException("User with ID $userId not found.")
            }
            val book = bookRepository.findById(bookId).orElseThrow {
                IllegalArgumentException("Book with ID $bookId not found.")
            }

            val userBook = UserBooksEntity(user = user, book = book)
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