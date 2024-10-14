package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.Users
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<Users, UUID> {
}