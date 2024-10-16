package com.example.homebookshelfapi.external.gpt

import org.springframework.stereotype.Component

@Component
class GptRequestBuilder {
    fun buildBookRecommendationsRequest(storedBooks: List<String>): Map<String, Any> {
        val prompt = buildPrompt(storedBooks)
        return mapOf(
            "model" to "gpt-4o-mini",
            "messages" to listOf(
                mapOf("role" to "system", "content" to "Provide a list of book ISBNs."),
                mapOf("role" to "user", "content" to prompt)
            )
        )
    }

    internal fun buildPrompt(storedBooks: List<String>): String {
        return """
            I have the following books in my collection: ${storedBooks.joinToString(", ")}.
            Recommend 5 similar books and return only their ISBNs in a comma-separated format with no additional text.
        """.trimIndent()
    }
}
