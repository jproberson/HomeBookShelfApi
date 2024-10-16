package com.example.homebookshelfapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    @Value("\${gpt.api-token}") private val apiToken: String
) {

    @Bean
    fun gptWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .defaultHeader("Authorization", "Bearer $apiToken")
            .defaultHeader("Content-Type", "application/json")
            .build()
    }
}
