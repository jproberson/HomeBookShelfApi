package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.entities.RecommendedBooksEntity
import com.example.homebookshelfapi.domain.entities.UserEntity
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecommendedBooksRepository : JpaRepository<RecommendedBooksEntity, UUID> {
  fun findByUser(user: UserEntity): List<RecommendedBooksEntity>
}
