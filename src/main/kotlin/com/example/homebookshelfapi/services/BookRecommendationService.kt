package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.dto.RecommendationResponse
import com.example.homebookshelfapi.domain.entities.BookEntity
import org.springframework.http.ResponseEntity
import java.util.*

interface BookRecommendationService {
    fun fetchMoreRecommendations(userId: UUID):ResponseEntity<RecommendationResponse>
    fun getRecommendations(userId: UUID, fetchMore: Boolean = false):ResponseEntity<RecommendationResponse>
    fun removeRecommendedBookForUser(userId: UUID, savedBook: BookEntity): ResponseEntity<BookEntity?>
}
