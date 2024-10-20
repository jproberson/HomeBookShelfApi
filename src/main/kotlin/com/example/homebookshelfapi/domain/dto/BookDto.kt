package com.example.homebookshelfapi.domain.dto

import java.time.LocalDate
import java.util.*

data class BookDto(
    val id: UUID,
    val isbn: String,
    val title: String,
    val authors: String,
    val description: String? = null,
    val categories: String? = null,
    val publishedDate: LocalDate? = null,
    val pageCount: Int? = null,
    val thumbnail: String? = null
)
