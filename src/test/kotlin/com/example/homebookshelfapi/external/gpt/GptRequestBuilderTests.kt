package com.example.homebookshelfapi.external.gpt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GptRequestBuilderTests {

    @Test
    fun `build book recommendations request should return gpt request`() {
        val storedBooks = listOf("book1", "book2", "book3")
        val gptRequestBuilder = GptRequestBuilder()

        val result = gptRequestBuilder.buildBookRecommendationsRequest(storedBooks)

        val expected = mapOf(
            "model" to "gpt-4o-mini",
            "messages" to listOf(
                mapOf("role" to "system", "content" to "Provide a list of book ISBNs."),
                mapOf(
                    "role" to "user", "content" to """
                    I have the following books in my collection: book1, book2, book3.
                    Recommend 5 similar books and return only their ISBNs in a comma-separated format with no additional text.
                """.trimIndent()
                )
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun `build prompt should return formatted prompt`() {
        val storedBooks = listOf("book1", "book2", "book3")
        val gptRequestBuilder = GptRequestBuilder()

        val result = gptRequestBuilder.buildPrompt(storedBooks)

        val expected = """
            I have the following books in my collection: book1, book2, book3.
            Recommend 5 similar books and return only their ISBNs in a comma-separated format with no additional text.
        """.trimIndent()
        assertEquals(expected, result)
    }

}