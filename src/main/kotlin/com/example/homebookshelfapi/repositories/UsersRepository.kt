package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.entities.UserEntity
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
  fun findByUsername(username: String): UserEntity?

  fun deleteByUsername(username: String)
}
