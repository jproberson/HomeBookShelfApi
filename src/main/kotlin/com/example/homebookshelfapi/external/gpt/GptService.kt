package com.example.homebookshelfapi.external.gpt

import org.springframework.http.ResponseEntity

interface GptService {
  fun getBookRecommendations(storedBooks: List<String>): ResponseEntity<List<String>>

  fun isAvailable(): Boolean
}
