package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.BookEntity
import java.util.*

interface BookService {

    fun getAllBooks(): List<BookEntity>

    fun getAllBooksByUserId(userId: UUID): List<BookEntity>

    fun getBookById(id: UUID): BookEntity?

    fun getBookByIsbn(isbn: String): BookEntity?

    fun addBook(bookEntity: BookEntity): BookEntity

    fun addBookToUserByIsbn(isbn: String, userId: UUID): BookEntity

    fun addBookByIsbn(isbn: String): BookEntity

    fun updateBook(id: UUID, updatedBookEntity: BookEntity): BookEntity?

    fun deleteBook(bookId: UUID, userId: UUID): Boolean
}
