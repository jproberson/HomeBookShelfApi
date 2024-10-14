package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.external.google.GoogleApiService
import com.example.homebookshelfapi.domain.Book
import com.example.homebookshelfapi.repositories.BookRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BookService(private val bookRepository: BookRepository, private val googleApiService: GoogleApiService) {

    fun getAllBooks(): List<Book> {
        return bookRepository.findAll()
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

    fun addBookByIsbn(isbn: String): Book {
        val existingBook = bookRepository.findByIsbn(isbn)
        if (existingBook.isPresent) {
            throw IllegalArgumentException("A book with this ISBN already exists")
        }

        val book = googleApiService.fetchBookInfoByISBN(isbn) ?: throw IllegalArgumentException("Book not found");

        return bookRepository.save(book)
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
