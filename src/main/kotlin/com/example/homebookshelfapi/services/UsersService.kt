package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.*

interface UsersService {
    fun getAllUsers(): List<UserEntity>
    fun getUserById(id: UUID): UserEntity?
    fun addUser(userEntity: UserEntity): UserEntity
    fun updateUser(id: UUID, updatedUserEntity: UserEntity): UserEntity?
    fun deleteUser(id: UUID): Boolean
}