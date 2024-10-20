package com.example.homebookshelfapi.exceptions

class GptApiException(message: String) : RuntimeException(message)

class UserNotFoundException(message: String) : RuntimeException(message)
