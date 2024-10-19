package com.example.homebookshelfapi.services.impl

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.domain.entities.UserBooksEntity
import com.example.homebookshelfapi.repositories.BookRepository
import com.example.homebookshelfapi.repositories.UserBooksRepository
import com.example.homebookshelfapi.repositories.UserRepository
import com.example.homebookshelfapi.services.UserBooksService
import jakarta.transaction.Transactional
import java.util.*
import org.springframework.stereotype.Service

@Service
class UserBooksServiceImpl(
  private val userBookRepository: UserBooksRepository,
  private val bookRepository: BookRepository,
  private val userRepository: UserRepository
) : UserBooksService {

  override fun getUserBooks(username: String): List<BookEntity> {
    val user =
      userRepository.findByUsername(username)
        ?: throw IllegalArgumentException("User with username $username not found.")

    return userBookRepository.findBooksByUserId(user.id)
  }

  override fun addBookToUser(username: String, bookId: UUID) {
    val user =
      userRepository.findByUsername(username)
        ?: throw IllegalArgumentException("User with username $username not found.")

    if (!userBookRepository.existsByUserAndBookId(user, bookId)) {
      val book =
        bookRepository.findById(bookId).orElseThrow {
          IllegalArgumentException("Book with ID $bookId not found.")
        }

      val userBook = UserBooksEntity(user = user, book = book)
      userBookRepository.save(userBook)
    }
  }

  @Transactional
  override fun deleteBookForUser(username: String, bookId: UUID): Boolean {
    val user =
      userRepository.findByUsername(username)
        ?: throw IllegalArgumentException("User with username $username not found.")

    return if (userBookRepository.existsByUserAndBookId(user, bookId)) {
      userBookRepository.deleteByUserAndBookId(user, bookId)
      true
    } else {
      false
    }
  }
}
