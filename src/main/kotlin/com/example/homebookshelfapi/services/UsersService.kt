package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.Users
import com.example.homebookshelfapi.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UsersService(private val userRepository: UserRepository) {
    fun getAllUsers(): List<Users>
    {
        return userRepository.findAll()
    }

    fun getUserById(id: UUID): Users?
    {
        return userRepository.findById(id).orElse(null)
    }

    fun addUser(users: Users): Users
    {
        return userRepository.save(users)
    }

    fun updateUser(id: UUID, updatedUsers: Users): Users?
    {
        return if (userRepository.existsById(id))
        {
            userRepository.save(updatedUsers.copy(id = id))
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