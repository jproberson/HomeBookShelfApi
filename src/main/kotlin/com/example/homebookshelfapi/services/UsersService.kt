package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.UserEntity

interface UsersService {
  fun getAllUsers(): List<UserEntity>

  fun getByUsername(username: String): UserEntity?

  fun addUser(userEntity: UserEntity): UserEntity

  fun deleteUser(username: String): Boolean
}
