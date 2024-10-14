package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.external.google.GoogleApiService
import com.example.homebookshelfapi.domain.Book
import com.example.homebookshelfapi.repositories.BookRepository
import com.example.homebookshelfapi.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val googleApiService: GoogleApiService,
    private val userBooksService: UserBooksService,
    private val userRepository: UserRepository
) {

    fun getAllBooks(): List<Book> {
        return bookRepository.findAll()
    }

    fun getAllBooksByUserId(userId: UUID): List<Book> {
        return userBooksService.getUserBooks(userId)
    }

    fun getBookById(id: UUID): Book? {
        return bookRepository.findById(id).orElse(null)
    }

    fun getBookByIsbn(isbn: String): Book? {
        return bookRepository.findByIsbn(isbn).orElse(null)
    }

    fun addBook(book: Book): Book {
        val existingBook = bookRepository.findByIsbn(book.isbn)
        if (existingBook.isPresent) {
            throw IllegalArgumentException("A book with this ISBN already exists")
        }
        return bookRepository.save(book)
    }

    fun addBookByIsbn(isbn: String, userId: UUID): Book {

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


    fun updateBook(id: UUID, updatedBook: Book): Book? {
        return if (bookRepository.existsById(id)) {
            bookRepository.save(updatedBook.copy(id = id))
        } else {
            null
        }
    }

    fun deleteBook(id: UUID): Boolean {
        return if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
