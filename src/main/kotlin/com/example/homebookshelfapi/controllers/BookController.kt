package com.example.homebookshelfapi.controllers

import com.example.homebookshelfapi.config.Constants.DEFAULT_USER_ID
import com.example.homebookshelfapi.domain.dto.BookDto
import com.example.homebookshelfapi.services.BookService
import com.example.homebookshelfapi.toBookDto
import com.example.homebookshelfapi.toBookEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/api/books")
class BookController(private val bookService: BookService) {

    @GetMapping
    fun getAllBooks(): List<BookDto> = bookService.getAllBooks().map { it.toBookDto() }

    @GetMapping("/user/{userId}")
    //TODO: Implement concept of multiple users
    fun getAllBooksByUser(@PathVariable userId: UUID): List<BookDto> =
        bookService.getAllBooksByUserId(DEFAULT_USER_ID).map { it.toBookDto() }

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: UUID): ResponseEntity<BookDto> {
        val book = bookService.getBookById(id)
        return if (book != null) {
            ResponseEntity.ok(book.toBookDto())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/isbn/{isbn}")
    fun getBookByIsbn(@PathVariable isbn: String): ResponseEntity<BookDto> {
        val book = bookService.getBookByIsbn(isbn)
        return if (book != null) {
            ResponseEntity.ok(book.toBookDto())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun addBook(@RequestBody book: BookDto): ResponseEntity<BookDto> {
        val newBook = bookService.addBook(book.toBookEntity())
        return ResponseEntity.status(HttpStatus.CREATED).body(newBook.toBookDto())
    }

    @PostMapping("/isbn/{isbn}")
    fun addBookToUserByIsbn(@PathVariable isbn: String): ResponseEntity<BookDto> {
        val newBook = bookService.addBookToUserByIsbn(isbn, DEFAULT_USER_ID)
        return ResponseEntity.status(HttpStatus.CREATED).body(newBook.toBookDto())
    }

    @PutMapping("/{id}")
    fun updateBook(@PathVariable id: UUID, @RequestBody updatedBook: BookDto): ResponseEntity<BookDto> {
        val book = bookService.updateBook(id, updatedBook.toBookEntity())
        return if (book != null) {
            ResponseEntity.ok(book.toBookDto())
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
