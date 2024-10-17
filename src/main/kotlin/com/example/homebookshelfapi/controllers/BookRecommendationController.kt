package com.example.homebookshelfapi.controllers

import com.example.homebookshelfapi.external.gpt.GptService
import com.example.homebookshelfapi.services.BookRecommendationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/api/recommend")
class RecommendationController(
    private val bookRecommendationService: BookRecommendationService,
    private val gptService: GptService
) {

    @GetMapping("/{userId}")
    fun getRecommendations(
        @PathVariable userId: UUID,
        @RequestParam(required = false, defaultValue = "false") more: Boolean
    ): ResponseEntity<Map<String, Any>> {
        val isGptAvailable = gptService.isAvailable()
        val recommendations = if (more) {
            bookRecommendationService.fetchMoreRecommendations(userId).body
        } else {
            bookRecommendationService.getRecommendations(userId).body
        }

        return ResponseEntity.ok(
            mapOf("books" to (recommendations ?: emptyList()), "gptAvailable" to isGptAvailable)
        )
    }

}
