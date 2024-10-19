package com.example.homebookshelfapi.domain.dto

import com.example.homebookshelfapi.domain.entities.Role
import java.util.*

data class UserDto(
    val id: UUID,
    val username: String,
    val password: String,
    val enabled: String,
    val role: Role,
    val createdAt: Date,
    val updatedAt: Date
)


data class UserResponse(
    val uuid: UUID,
    val username: String,
)

data class UserRequest(
    val username: String,
    val password: String,
)