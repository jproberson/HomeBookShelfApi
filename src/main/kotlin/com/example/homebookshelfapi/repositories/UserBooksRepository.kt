package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.UserBooks
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserBooksRepository : JpaRepository<UserBooks, UUID> {
    fun existsByUserIdAndBookId(userId: UUID, bookId: UUID): Boolean
    fun deleteByUserIdAndBookId(userId: UUID, bookId: UUID)
    fun findByUserId(userId: UUID): List<UserBooks>
}