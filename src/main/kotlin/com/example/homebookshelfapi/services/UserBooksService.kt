package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.BookEntity
import java.util.*

interface UserBooksService {
    fun getUserBooks(userId: UUID): List<BookEntity>
    fun addBookToUser(userId: UUID, bookId: UUID)
    fun deleteBookForUser(userId: UUID, bookId: UUID): Boolean
}
