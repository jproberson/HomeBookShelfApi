package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.entities.UserEntity
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@DataJpaTest
@Transactional
class UserEntityRepositoryTests {
    // TODO: Switch to Test Containers?

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var user: UserEntity

    @BeforeEach
    fun setup() {
        user = UserEntity(name = "Jake")
    }

    @Test
    fun saveUser_ShouldSaveAndReturnUser() {
        val savedUser = userRepository.save(user)

        assertNotNull(savedUser.id)
        assertEquals("Jake", savedUser.name)
    }

    @Test
    fun findUserById_ShouldReturnUserWhenFound() {
        val savedUser = userRepository.save(user)
        val foundUser = userRepository.findByIdOrNull(savedUser.id)

        assertNotNull(foundUser)
        assertEquals(savedUser.name, foundUser.name)
    }

    @Test
    fun findAllUsers_ShouldReturnAllUsers() {
        userRepository.save(user)
        val users = userRepository.findAll()

        assertNotNull(users)
        assertEquals(1, users.size)
    }

    @Test
    fun deleteUser_ShouldRemoveUserById() {
        val savedUser = userRepository.save(user)
        userRepository.deleteById(savedUser.id)

        val users = userRepository.findAll()

        assertEquals(0, users.size)
    }
}
