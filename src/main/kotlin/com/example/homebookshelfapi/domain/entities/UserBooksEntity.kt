package com.example.homebookshelfapi.domain.entities

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "user_books")
data class UserBooksEntity(
  @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: UUID = UUID.randomUUID(),
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  val user: UserEntity,
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  val book: BookEntity,
  @Column(nullable = false) val addedAt: Date = Date()
)
