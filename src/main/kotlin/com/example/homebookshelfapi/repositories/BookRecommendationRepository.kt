package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.entities.RecommendedBooksEntity
import com.example.homebookshelfapi.domain.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BookRecommendationRepository : JpaRepository<RecommendedBooksEntity, UUID> {
    fun findByUser(user: UserEntity): List<RecommendedBooksEntity>
}