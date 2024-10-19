package com.example.homebookshelfapi.repositories.security

import com.example.homebookshelfapi.domain.entities.RefreshTokenEntity
import com.example.homebookshelfapi.domain.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {
  fun findByToken(token: String): RefreshTokenEntity?

  @Query(
    "SELECT t FROM RefreshTokenEntity t WHERE t.user = :user AND t.revoked = false AND t.expirationDate > CURRENT_TIMESTAMP"
  )
  fun findActiveTokensByUser(user: UserEntity): List<RefreshTokenEntity>
}
