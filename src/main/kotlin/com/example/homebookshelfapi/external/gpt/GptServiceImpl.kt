package com.example.homebookshelfapi.external.gpt

import com.example.homebookshelfapi.exceptions.GptApiException
import com.example.homebookshelfapi.utils.logger
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class GptServiceImpl(
  private val gptApiClient: GptApiClient,
  private val gptRequestBuilder: GptRequestBuilder,
) : GptService {

  private val logger = logger<GptService>()

  override fun getBookRecommendations(storedBooks: List<String>): ResponseEntity<List<String>> {
    if (storedBooks.isEmpty()) {
      logger.error("Empty list of storedBooks provided")
      throw IllegalArgumentException("storedBooks cannot be empty")
    }

    val requestPayload = gptRequestBuilder.buildBookRecommendationsRequest(storedBooks)

    return try {
      logger.info("Sending GPT-4 API request for book recommendations")
      val response = gptApiClient.postGptRequest(requestPayload)
      logger.info("Received GPT-4 API response")

      val messageContent =
        response.choices.firstOrNull()?.message?.content
          ?: throw GptApiException("No message content found in GPT response")
      val recommendations = parseResponse(messageContent)
      ResponseEntity.ok(recommendations)
    } catch (ex: Exception) {
      logger.error("Error occurred while fetching GPT-4 recommendations", ex)
      throw GptApiException("Failed to get recommendations: ${ex.message}")
    }
  }

  override fun isAvailable(): Boolean {
    return gptApiClient.isGptRestTemplateAvailable()
  }

  internal fun parseResponse(response: String): List<String> {
    return response.split(",").map { it.trim() }.filter { it.isNotBlank() }
  }
}
