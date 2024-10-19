package com.example.homebookshelfapi.controllers

import com.example.homebookshelfapi.domain.dto.BookDto
import com.example.homebookshelfapi.domain.dto.RecommendationResponse
import com.example.homebookshelfapi.services.BookRecommendationService
import com.example.homebookshelfapi.services.BookService
import com.example.homebookshelfapi.toBookDto
import com.example.homebookshelfapi.toBookEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/api/books")
class BookController(
    private val bookService: BookService,
    private val bookRecommendationService: BookRecommendationService,
) {

    @GetMapping
    fun getAllBooks(): List<BookDto> = bookService.getAllBooks().map { it.toBookDto() }

    @GetMapping("/user/{username}")
    fun getAllBooksByUser(@PathVariable username: String): List<BookDto> =
        bookService.getAllBooksByUsername(username).map { it.toBookDto() }

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
    fun addBookToUserByIsbn(@PathVariable isbn: String, authentication: Authentication): ResponseEntity<BookDto> {
        val username = authentication.name
        val newBook = bookService.addBookToUserByIsbn(isbn, username)
        bookRecommendationService.removeRecommendedBookForUser(username, newBook)
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
    fun deleteBook(@PathVariable bookId: UUID, authentication: Authentication): ResponseEntity<Void> {
        val username = authentication.name

        return if (bookService.deleteBook(bookId, username)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/recommendations/{username}")
    fun getRecommendations(
        @PathVariable username: String,
        @RequestParam(required = false, defaultValue = "false") more: Boolean
    ): ResponseEntity<RecommendationResponse> {
        val recommendations =
            bookRecommendationService.getRecommendationsForUser(username, more).body

        return ResponseEntity.ok(recommendations)
    }
}
