package com.example.homebookshelfapi.controllers

import com.example.homebookshelfapi.domain.dto.BookDto
import com.example.homebookshelfapi.domain.dto.RecommendationResponse
import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.exceptions.UserNotFoundException
import com.example.homebookshelfapi.services.BookService
import com.example.homebookshelfapi.services.RecommendedBookService
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
    private val recommendedBookService: RecommendedBookService,
) {

    @GetMapping
    fun getAllBooks(): List<BookDto> = bookService.getAllBooks().map { it.toBookDto() }


    @GetMapping("/user/{username}")
    fun getAllBooksByUser(@PathVariable username: String): List<BookDto> {
        val books = bookService.getAllBooksByUsername(username).map { it.toBookDto() }
        return books
    }

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: UUID): ResponseEntity<BookDto> {
        val book = bookService.getBookById(id)
        return if (book != null) {
            ResponseEntity.ok(book.toBookDto())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{id}")
    fun updateBook(
        @PathVariable id: UUID,
        @RequestBody updatedBook: BookDto
    ): ResponseEntity<BookDto> {
        val book = bookService.updateBook(id, updatedBook.toBookEntity())
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

    @PostMapping("/isbn/{isbn}")
    fun addBookToUserByIsbn(
        @PathVariable isbn: String,
        authentication: Authentication
    ): ResponseEntity<BookDto> {
        val username = authentication.name
        val newBook = bookService.addBookToUserByIsbn(isbn, username)
        recommendedBookService.removeRecommendedBookForUser(username, newBook)
        return ResponseEntity.status(HttpStatus.CREATED).body(newBook.toBookDto())
    }

    @DeleteMapping("/{bookId}")
    fun deleteBook(@PathVariable bookId: UUID, authentication: Authentication): ResponseEntity<BookEntity> {
        val username = authentication.name
        return try {
            val deletedBook = bookService.deleteBookForUser(bookId, username)
            if (deletedBook != null) {
                ResponseEntity.ok(deletedBook)
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (ex: UserNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/recommendations/{username}")
    fun getRecommendations(
        @PathVariable username: String,
        @RequestParam(required = false, defaultValue = "false") more: Boolean
    ): ResponseEntity<RecommendationResponse> {
        val recommendations = recommendedBookService.getRecommendationsForUser(username, more)

        return ResponseEntity.ok(recommendations)
    }
}
