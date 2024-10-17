package com.example.homebookshelfapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig(@Value("\${gpt.api-token}") private val apiToken: String) {

    @Bean
    fun gptRestTemplate(): RestTemplate? {
        if (apiToken == "default") {
            return null
        }

        val restTemplate = RestTemplate()
        val interceptors = restTemplate.interceptors
        interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
            request.headers.set("Authorization", "Bearer $apiToken")
            request.headers.set("Content-Type", "application/json")
            execution.execute(request, body)
        })
        restTemplate.interceptors = interceptors

        return restTemplate
    }
}
