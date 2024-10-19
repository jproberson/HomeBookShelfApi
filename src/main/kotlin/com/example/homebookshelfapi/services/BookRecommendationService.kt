package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.dto.RecommendationResponse
import com.example.homebookshelfapi.domain.entities.BookEntity
import org.springframework.http.ResponseEntity

interface BookRecommendationService {
    fun getRecommendationsForUser(username: String, fetchMore: Boolean = false): ResponseEntity<RecommendationResponse>
    fun removeRecommendedBookForUser(username: String, savedBook: BookEntity): ResponseEntity<BookEntity?>
}
