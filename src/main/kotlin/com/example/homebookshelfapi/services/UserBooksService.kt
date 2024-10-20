package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.BookEntity
import java.util.*

interface UserBooksService {
    fun getUserBooks(username: String): List<BookEntity>

    fun addBookToUser(username: String, bookId: UUID)

    fun deleteBookForUser(username: String, bookId: UUID): Boolean
}
