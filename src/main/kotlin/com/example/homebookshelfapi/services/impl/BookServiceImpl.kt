package com.example.homebookshelfapi.services.impl

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.external.google.GoogleApiService
import com.example.homebookshelfapi.repositories.BookRepository
import com.example.homebookshelfapi.services.BookService
import com.example.homebookshelfapi.services.UserBooksService
import com.example.homebookshelfapi.utils.logger
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class BookServiceImpl(
    private val bookRepository: BookRepository,
    private val googleApiService: GoogleApiService,
    private val userBooksService: UserBooksService,
) : BookService {

    private val logger = logger<BookServiceImpl>()

    override fun getAllBooks(): List<BookEntity> {
        return bookRepository.findAll()
    }

    override fun getAllBooksByUsername(username: String): List<BookEntity> {
        return userBooksService.getUserBooks(username)
    }

    override fun getBookById(id: UUID): BookEntity? {
        return bookRepository.findByIdOrNull(id)
    }

    override fun getBookByIsbn(isbn: String): BookEntity? {
        return bookRepository.findByIsbn(isbn).orElse(null)
    }

    override fun addBook(bookEntity: BookEntity): BookEntity {
        val existingBook = bookRepository.findByIsbn(bookEntity.isbn)
        if (existingBook.isPresent) {
            throw IllegalArgumentException("A book with this ISBN already exists")
        }
        return bookRepository.save(bookEntity)
    }

    override fun addBookToUserByIsbn(isbn: String, username: String): BookEntity {
        val existingBook = bookRepository.findByIsbn(isbn)

        if (existingBook.isPresent) {
            userBooksService.addBookToUser(username, existingBook.get().id)
            return existingBook.get()
        }

        val fetchedBook = googleApiService.fetchBookInfoByISBN(isbn) ?: throw IllegalArgumentException("Book not found")
        val savedBook = bookRepository.save(fetchedBook)

        userBooksService.addBookToUser(username, savedBook.id)

        return savedBook
    }

    override fun addBookByIsbn(isbn: String): BookEntity? {
        val existingBook = bookRepository.findByIsbn(isbn)

        if (existingBook.isPresent) {
            return existingBook.get()
        }

        val fetchedBook = googleApiService.fetchBookInfoByISBN(isbn)
        if (fetchedBook == null) {
            logger.error("Book ${isbn} not found")
            return null
        }
        return bookRepository.save(fetchedBook)
    }

    override fun updateBook(id: UUID, updatedBookEntity: BookEntity): BookEntity? {
        return if (bookRepository.existsById(id)) {
            bookRepository.save(updatedBookEntity.copy(id = id))
        } else {
            null
        }
    }

    override fun deleteBook(bookId: UUID, username: String): Boolean {
        val userBooks = userBooksService.getUserBooks(username)

        return if (userBooks.any { it.id == bookId }) {
            userBooksService.deleteBookForUser(username, bookId)
            true
        } else {
            false
        }
    }
}
