package com.example.homebookshelfapi.domain.entities

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "recommended_books")
data class RecommendedBooksEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: UUID = UUID.randomUUID(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    val book: BookEntity,
    @Column(name = "recommended_at", nullable = false) val recommendedAt: Date = Date(),
    @Column(name = "recommendation_strategy", nullable = true)
    val recommendationStrategy: String = "default"
)
