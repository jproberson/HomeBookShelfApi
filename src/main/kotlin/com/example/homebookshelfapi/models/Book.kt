package com.example.homebookshelfapi.models

import jakarta.persistence.*
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
    val author: String,

    @Column(nullable = true)
    val publishedYear: Int? = null
)