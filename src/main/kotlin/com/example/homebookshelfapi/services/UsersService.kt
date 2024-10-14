package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.User
import com.example.homebookshelfapi.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UsersService(private val userRepository: UserRepository) {
    fun getAllUsers(): List<User>
    {
        return userRepository.findAll()
    }

    fun getUserById(id: UUID): User?
    {
        return userRepository.findById(id).orElse(null)
    }

    fun addUser(user: User): User
    {
        return userRepository.save(user)
    }

    fun updateUser(id: UUID, updatedUser: User): User?
    {
        return if (userRepository.existsById(id))
        {
            userRepository.save(updatedUser.copy(id = id))
        }
        else
        {
            null
        }
    }

    fun deleteUser(id: UUID): Boolean
    {
        return if (userRepository.existsById(id))
        {
            userRepository.deleteById(id)
            true
        }
        else
        {
            false
        }
    }
}