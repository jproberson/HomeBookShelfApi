package com.example.homebookshelfapi.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "user_books")
data class UserBooks(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "book_id", nullable = false)
    val bookId: UUID,

    @Column(nullable = false)
    val addedAt: Date = Date()
)
