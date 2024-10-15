package com.example.homebookshelfapi.controllers

import com.example.homebookshelfapi.external.gpt.GptService
import com.example.homebookshelfapi.services.UserBooksService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/v1/api/recommend")
class RecommendationController(
    private val gptService: GptService,
    private val userBooksService: UserBooksService
) {

    @GetMapping("/{userId}")
    fun getRecommendations(@PathVariable userId: UUID): Mono<ResponseEntity<List<String>>> {
        val userBooks = userBooksService.getUserBooks(userId).map { it.title }
        return gptService.getBookRecommendations(userBooks)
            .map { recommendations -> ResponseEntity.ok(recommendations) }
    }
}
