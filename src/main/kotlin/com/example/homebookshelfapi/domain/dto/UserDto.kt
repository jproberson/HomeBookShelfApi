package com.example.homebookshelfapi.domain.dto

import java.util.*

data class UserDto(
    val id: UUID,
    val name: String,
    val createdAt: Date
)