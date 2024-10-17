package com.example.homebookshelfapi.external.gpt

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class GptApiClient(private val gptWebClient: WebClient?) {
    fun postGptRequest(payload: Map<String, Any>): Mono<GptResponse> {
        return if (gptWebClient == null) {
            Mono.error(IllegalStateException("GPT_API_TOKEN is not configured."))
        } else {
            gptWebClient.post()
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(GptResponse::class.java)
        }
    }
    fun isGptWebClientAvailable(): Boolean {
        return gptWebClient != null
    }
}


