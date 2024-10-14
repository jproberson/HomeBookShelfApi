package com.example.homebookshelfapi.controllers

import com.example.homebookshelfapi.config.Constants.DEFAULT_USER_ID
import com.example.homebookshelfapi.domain.Book
import com.example.homebookshelfapi.services.BookService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/v1/api/books")
class BookController(private val bookService: BookService) {

    @GetMapping
    fun getAllBooks(): List<Book> = bookService.getAllBooks()

    @GetMapping("/user/{userId}")
    //TODO: Implement concept of multiple users
    fun getAllBooksByUser(@PathVariable userId: UUID): List<Book> = bookService.getAllBooksByUserId(DEFAULT_USER_ID)

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: UUID): ResponseEntity<Book> {
        val book = bookService.getBookById(id)
        return if (book != null) {
            ResponseEntity.ok(book)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/isbn/{isbn}")
    fun getBookByIsbn(@PathVariable isbn: String): ResponseEntity<Book> {
        val book = bookService.getBookByIsbn(isbn)
        return if (book != null) {
            ResponseEntity.ok(book)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun addBook(@RequestBody book: Book): ResponseEntity<Book> {
        val newBook = bookService.addBook(book)
        return ResponseEntity.status(HttpStatus.CREATED).body(newBook)
    }

    @PostMapping("/isbn/{isbn}")
    fun addBookByIsbn(@PathVariable isbn: String): ResponseEntity<Book> {
        val newBook = bookService.addBookByIsbn(isbn, DEFAULT_USER_ID)
        return ResponseEntity.status(HttpStatus.CREATED).body(newBook)
    }

    @PutMapping("/{id}")
    fun updateBook(@PathVariable id: UUID, @RequestBody updatedBook: Book): ResponseEntity<Book> {
        val book = bookService.updateBook(id, updatedBook)
        return if (book != null) {
            ResponseEntity.ok(book)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{bookId}")
    fun deleteBook(@PathVariable bookId: UUID): ResponseEntity<Void> {
        return if (bookService.deleteBook(bookId, DEFAULT_USER_ID)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
