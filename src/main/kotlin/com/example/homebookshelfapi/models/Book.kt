package com.example.homebookshelfapi.models

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    val isbn: String,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val authors: String,

    @Column(nullable = true)
    val description: String? = null,

    @Column(nullable = true)
    val categories: String? = null,

    @Column(nullable = true)
    val publishedDate: LocalDate? = null,

    @Column(nullable = true)
    val pageCount: Int? = null,

    @Column(nullable = true)
    val thumbnail: String? = null
)
