package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.BookEntity
import java.util.*

interface BookService {

    fun getAllBooks(): List<BookEntity>

    fun getAllBooksByUsername(username: String): List<BookEntity>

    fun getBookById(id: UUID): BookEntity?

    fun getBookByIsbn(isbn: String): BookEntity?

    fun addBook(bookEntity: BookEntity): BookEntity

    fun addBookToUserByIsbn(isbn: String, username: String): BookEntity

    fun addBookByIsbn(isbn: String): BookEntity?


    fun updateBook(id: UUID, updatedBookEntity: BookEntity): BookEntity?

    fun deleteBook(bookId: UUID, username: String): Boolean
}
