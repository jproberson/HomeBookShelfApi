package com.example.homebookshelfapi.domain.entities

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "refresh_token")
data class RefreshTokenEntity(
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  val user: UserEntity,
  val token: String,
  val expirationDate: Date,
  val revoked: Boolean = false
)
