package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.BookEntity
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.*

interface BookRecommendationService {
    fun fetchMoreRecommendations(userId: UUID): Mono<ResponseEntity<List<BookEntity>>>
    fun getRecommendations(userId: UUID, fetchMore: Boolean = false): Mono<ResponseEntity<List<BookEntity>>>
}
