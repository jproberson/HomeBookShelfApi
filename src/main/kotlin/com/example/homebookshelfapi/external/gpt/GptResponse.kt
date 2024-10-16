package com.example.homebookshelfapi.external.gpt


data class GptChoice(
    val message: GptMessage
)

data class GptMessage(
    val content: String
)

data class GptResponse(
    val choices: List<GptChoice>
)
