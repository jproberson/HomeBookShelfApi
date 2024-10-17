package com.example.homebookshelfapi

import com.example.homebookshelfapi.domain.dto.BookDto
import com.example.homebookshelfapi.domain.dto.UserDto
import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.domain.entities.UserEntity

fun BookEntity.toBookDto() = BookDto(
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

fun BookDto.toBookEntity() = BookEntity(
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

fun UserEntity.toUserDto() = UserDto(
    id = this.id,
    name = this.name,
    createdAt = this.createdAt
)

fun UserDto.toUserEntity() = UserEntity(
    id = this.id,
    name = this.name,
    createdAt = this.createdAt
)
