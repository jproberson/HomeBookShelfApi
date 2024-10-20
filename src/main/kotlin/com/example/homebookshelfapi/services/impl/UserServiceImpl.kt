package com.example.homebookshelfapi.services.impl

import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.repositories.UserRepository
import com.example.homebookshelfapi.services.UsersService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsersServiceImpl(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder
) : UsersService {
    override fun getAllUsers(): List<UserEntity> {
        return userRepository.findAll()
    }

    override fun getByUsername(username: String): UserEntity? {
        return userRepository.findByUsername(username)
    }

    override fun addUser(userEntity: UserEntity): UserEntity {
        val updated = userEntity.copy(password = encoder.encode(userEntity.password))
        return userRepository.save(updated)
    }

    override fun deleteUser(username: String): Boolean {
        val user = userRepository.findByUsername(username)
        return if (user != null) {
            userRepository.deleteByUsername(username)
            true
        } else {
            false
        }
    }
}
