package com.example.homebookshelfapi.external.gpt

import reactor.core.publisher.Mono

interface GptService {
    fun getBookRecommendations(storedBooks: List<String>): Mono<List<String>>
};

