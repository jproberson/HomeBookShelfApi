package com.example.homebookshelfapi.external.gpt

data class GptChoice(val message: GptMessage)

data class GptResponse(val choices: List<GptChoice>)

data class GptMessage(val role: String, val content: String)

data class GptRequest(val model: String, val messages: List<GptMessage>)
