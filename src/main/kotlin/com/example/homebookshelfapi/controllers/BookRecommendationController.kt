package com.example.homebookshelfapi.controllers

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.external.gpt.GptService
import com.example.homebookshelfapi.services.BookRecommendationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/v1/api/recommend")
class RecommendationController(
    private val bookRecommendationService: BookRecommendationService,
    private val gptService: GptService
) {

    @GetMapping("/{userId}")
    fun getRecommendations(@PathVariable userId: UUID): ResponseEntity<Map<String, Any>> {
        val isGptAvailable = gptService.isAvailable()
        val recommendations = bookRecommendationService.getRecommendations(userId)

        return ResponseEntity.ok(
            mapOf("books" to recommendations, "gptEnabled" to isGptAvailable)
        )
    }

    @GetMapping("/{userId}/more")
    fun getMoreRecommendations(@PathVariable userId: UUID): ResponseEntity<List<BookEntity>> {
        val isGptAvailable = gptService.isAvailable()
        if (!isGptAvailable) {
            return ResponseEntity.badRequest().build()
        }
        return bookRecommendationService.fetchMoreRecommendations(userId)
    }
}
