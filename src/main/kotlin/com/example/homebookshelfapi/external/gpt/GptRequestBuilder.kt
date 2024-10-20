package com.example.homebookshelfapi.external.gpt

import org.springframework.stereotype.Component

@Component
class GptRequestBuilder {
    fun buildBookRecommendationsRequest(storedBooks: List<String>): GptRequest {
        val prompt = buildPrompt(storedBooks)
        return GptRequest(
            model = "gpt-4o-mini",
            messages =
            listOf(GptMessage("system", "Provide a list of book ISBNs."), GptMessage("user", prompt))
        )
    }

    internal fun buildPrompt(storedBooks: List<String>): String {
        return """
            I have the following books in my collection: ${storedBooks.joinToString(", ")}.
            Recommend 5 similar books and return only their ISBNs in a comma-separated format with no additional text.
            Do not provide any duplicates or books that are already in my collection.
        """
            .trimIndent()
    }
}
