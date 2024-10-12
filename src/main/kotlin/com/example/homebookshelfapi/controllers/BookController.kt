package com.example.homebookshelfapi.controllers

import com.example.homebookshelfapi.models.Book
import com.example.homebookshelfapi.services.BookService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService) {

    @GetMapping
    fun getAllBooks(): List<Book> = bookService.getAllBooks()

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

    @PutMapping("/{id}")
    fun updateBook(@PathVariable id: UUID, @RequestBody updatedBook: Book): ResponseEntity<Book> {
        val book = bookService.updateBook(id, updatedBook)
        return if (book != null) {
            ResponseEntity.ok(book)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (bookService.deleteBook(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
