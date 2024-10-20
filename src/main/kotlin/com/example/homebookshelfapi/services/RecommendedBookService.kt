package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.dto.RecommendationResponse
import com.example.homebookshelfapi.domain.entities.BookEntity

interface RecommendedBookService {
    fun getRecommendationsForUser(
        username: String,
        fetchMore: Boolean = false
    ): RecommendationResponse

    fun removeRecommendedBookForUser(
        username: String,
        savedBook: BookEntity
    ): BookEntity?
}
