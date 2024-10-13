package com.example.homebookshelfapi.respositories

import com.example.homebookshelfapi.models.Users
import com.example.homebookshelfapi.repositories.UserRepository
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@DataJpaTest
@Transactional
class UsersRepositoryTests {
    // TODO: Switch to Test Containers?

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var users: Users

    @BeforeEach
    fun setup() {
        users = Users(name = "Jake")
    }

    @Test
    fun saveUser_ShouldSaveAndReturnUser() {
        val savedUser = userRepository.save(users)

        assertNotNull(savedUser.id)
        assertEquals("Jake", savedUser.name)
    }

    @Test
    fun findUserById_ShouldReturnUserWhenFound() {
        val savedUser = userRepository.save(users)
        val foundUser = userRepository.findById(savedUser.id)

        assertNotNull(foundUser)
        assertEquals(savedUser.name, foundUser.get().name)
    }

    @Test
    fun findAllUsers_ShouldReturnAllUsers() {
        userRepository.save(users)
        val users = userRepository.findAll()

        assertNotNull(users)
        assertEquals(1, users.size)
    }

    @Test
    fun deleteUser_ShouldRemoveUserById() {
        val savedUser = userRepository.save(users)
        userRepository.deleteById(savedUser.id)

        val users = userRepository.findAll()

        assertEquals(0, users.size)
    }
}
