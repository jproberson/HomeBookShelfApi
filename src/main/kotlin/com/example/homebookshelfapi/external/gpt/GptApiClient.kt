package com.example.homebookshelfapi.external.gpt

import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Component
class GptApiClient(private val gptRestTemplate: RestTemplate?) {
    fun postGptRequest(payload: Map<String, Any>): GptResponse {
        if (gptRestTemplate == null) {
            throw IllegalStateException("GPT_API_TOKEN is not configured.")
        }

        return try {
            val url = "https://api.openai.com/v1/chat/completions"
            gptRestTemplate.postForObject(url, payload, GptResponse::class.java)
                ?: throw IllegalStateException("No response from GPT API")
        } catch (e: HttpClientErrorException) {
            throw IllegalStateException("Error communicating with GPT API: ${e.message}")
        }
    }

    fun isGptRestTemplateAvailable(): Boolean {
        return gptRestTemplate != null
    }
}
