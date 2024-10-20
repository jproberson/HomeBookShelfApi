package com.example.homebookshelfapi

import com.example.homebookshelfapi.domain.dto.BookDto
import com.example.homebookshelfapi.domain.dto.UserDto
import com.example.homebookshelfapi.domain.dto.UserRequest
import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.domain.entities.Role
import com.example.homebookshelfapi.domain.entities.UserEntity
import java.util.*

fun BookEntity.toBookDto() =
    BookDto(
        id = this.id,
        isbn = this.isbn,
        title = this.title,
        authors = this.authors,
        description = this.description,
        categories = this.categories,
        publishedDate = this.publishedDate,
        pageCount = this.pageCount,
        thumbnail = this.thumbnail
    )

fun BookDto.toBookEntity() =
    BookEntity(
        id = this.id,
        isbn = this.isbn,
        title = this.title,
        authors = this.authors,
        description = this.description,
        categories = this.categories,
        publishedDate = this.publishedDate,
        pageCount = this.pageCount,
        thumbnail = this.thumbnail
    )

fun UserRequest.toUserEntity() =
    UserEntity(
        id = UUID.randomUUID(),
        username = this.username,
        password = this.password,
        enabled = true,
        role = Role.USER,
        createdAt = Date(),
        updatedAt = Date()
    )

fun UserEntity.toUserDto() =
    UserDto(
        id = this.id,
        username = this.username,
        password = this.password,
        enabled = this.enabled.toString(),
        role = this.role,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

fun UserDto.toUserEntity() =
    UserEntity(
        id = this.id,
        username = this.username,
        password = this.password,
        enabled = this.enabled.toBoolean(),
        role = this.role,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
