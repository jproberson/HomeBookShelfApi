package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.entities.UserBooksEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserBooksRepository : JpaRepository<UserBooksEntity, UUID> {
    fun existsByUserIdAndBookId(userId: UUID, bookId: UUID): Boolean
    fun deleteByUserIdAndBookId(userId: UUID, bookId: UUID)
    fun findByUserId(userId: UUID): List<UserBooksEntity>
}