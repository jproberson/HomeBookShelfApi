package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.external.google.GoogleApiService
import com.example.homebookshelfapi.repositories.BookRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class BookServiceImpl(
    private val bookRepository: BookRepository,
    private val googleApiService: GoogleApiService,
    private val userBooksService: UserBooksService,
) : BookService {

    override fun getAllBooks(): List<BookEntity> {
        return bookRepository.findAll()
    }

    override fun getAllBooksByUserId(userId: UUID): List<BookEntity> {
        return userBooksService.getUserBooks(userId)
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

    override fun addBookToUserByIsbn(isbn: String, userId: UUID): BookEntity {

        val existingBook = bookRepository.findByIsbn(isbn)

        if (existingBook.isPresent) {
            userBooksService.addBookToUser(userId, existingBook.get().id)
            return existingBook.get()
        }

        val fetchedBook = googleApiService.fetchBookInfoByISBN(isbn) ?: throw IllegalArgumentException("Book not found")

        val savedBook = bookRepository.save(fetchedBook)

        userBooksService.addBookToUser(userId, savedBook.id)

        return savedBook
    }

    override fun addBookByIsbn(isbn: String): BookEntity {
        val existingBook = bookRepository.findByIsbn(isbn)

        if (existingBook.isPresent) {
            return existingBook.get()
        }

        val fetchedBook = googleApiService.fetchBookInfoByISBN(isbn) ?: throw IllegalArgumentException("Book not found")

        return bookRepository.save(fetchedBook)
    }

    override fun updateBook(id: UUID, updatedBookEntity: BookEntity): BookEntity? {
        return if (bookRepository.existsById(id)) {
            bookRepository.save(updatedBookEntity.copy(id = id))
        } else {
            null
        }
    }

    override fun deleteBook(bookId: UUID, userId: UUID): Boolean {
        val userBooks = userBooksService.getUserBooks(userId)

        return if (userBooks.any { it.id == bookId }) {
            userBooksService.deleteBookForUser(userId, bookId)
            true
        } else {
            false
        }
    }
}
