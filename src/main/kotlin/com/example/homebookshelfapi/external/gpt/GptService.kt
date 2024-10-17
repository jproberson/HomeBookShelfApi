package com.example.homebookshelfapi.external.gpt

import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono

interface GptService {
    fun getBookRecommendations(storedBooks: List<String>): Mono<ResponseEntity<List<String>>>
    fun isAvailable(): Boolean
}

