package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.BookEntity
import org.springframework.http.ResponseEntity
import java.util.*

interface BookRecommendationService {
    fun fetchMoreRecommendations(userId: UUID): ResponseEntity<List<BookEntity>>
    fun getRecommendations(userId: UUID, fetchMore: Boolean = false): ResponseEntity<List<BookEntity>>
}
