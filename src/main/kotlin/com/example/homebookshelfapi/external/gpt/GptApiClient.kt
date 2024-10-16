package com.example.homebookshelfapi.external.gpt

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class GptApiClient(private val webClient: WebClient) {

    fun postGptRequest(payload: Map<String, Any>): Mono<GptResponse> {
        return webClient.post()
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(GptResponse::class.java)
    }
}
