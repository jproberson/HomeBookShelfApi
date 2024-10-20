package com.example.homebookshelfapi.external.gpt

import generateBookEntity
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Profile("test", "integration")
@Service
class MockGptServiceImpl : GptService {
    override fun getBookRecommendations(storedBooks: List<String>): ResponseEntity<List<String>> {
        val generatedIsbns = mutableSetOf<String>()

        while (generatedIsbns.size < 3) {
            val newIsbn = generateBookEntity().isbn
            if (!storedBooks.contains(newIsbn)) {
                generatedIsbns.add(newIsbn)
            }
        }

        return ResponseEntity.ok(generatedIsbns.toList())
    }

    override fun isAvailable(): Boolean {
        return true
    }

}