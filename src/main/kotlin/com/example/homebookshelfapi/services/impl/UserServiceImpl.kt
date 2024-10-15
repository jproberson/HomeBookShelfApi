package com.example.homebookshelfapi.services.impl

import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.repositories.UserRepository
import com.example.homebookshelfapi.services.UsersService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class UsersServiceImpl(private val userRepository: UserRepository) : UsersService {
    override fun getAllUsers(): List<UserEntity> {
        return userRepository.findAll()
    }

    override fun getUserById(id: UUID): UserEntity? {
        return userRepository.findByIdOrNull(id)
    }

    override fun addUser(userEntity: UserEntity): UserEntity {
        return userRepository.save(userEntity)
    }

    override fun updateUser(id: UUID, updatedUserEntity: UserEntity): UserEntity? {
        return if (userRepository.existsById(id)) {
            userRepository.save(updatedUserEntity.copy(id = id))
        } else {
            null
        }
    }

    override fun deleteUser(id: UUID): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}