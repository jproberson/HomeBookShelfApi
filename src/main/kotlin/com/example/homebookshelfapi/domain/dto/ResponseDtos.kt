package com.example.homebookshelfapi.domain.dto

import com.example.homebookshelfapi.domain.entities.BookEntity

data class RecommendationResponse(
  val books: List<BookEntity>,
  val recommendationsAvailable: Boolean
)
