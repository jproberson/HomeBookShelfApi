package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.repositories.UserRepository
import com.example.homebookshelfapi.services.impl.UsersServiceImpl
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserEntityServiceTests {

    @MockK
    private lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var usersService: UsersServiceImpl

    private lateinit var user: UserEntity

    private lateinit var id: UUID

    @BeforeEach
    fun setup() {
        id = UUID.randomUUID()
        MockKAnnotations.init(this)
        user = UserEntity(id = id, name = "Jake")
    }

    @Test
    fun getAllUsers_ShouldReturnAllUsers() {
        every { userRepository.findAll() } returns listOf(user)

        val users = usersService.getAllUsers()

        assertNotNull(users)
        assertEquals(1, users.size)
        assertEquals("Jake", users[0].name)

        verify { userRepository.findAll() }
    }

    @Test
    fun getUserById_ShouldReturnUserWhenFound() {
        every { userRepository.findByIdOrNull(user.id) } returns user

        val foundUser = usersService.getUserById(user.id)

        assertNotNull(foundUser)
        assertEquals("Jake", foundUser.name)

        verify { userRepository.findByIdOrNull(user.id) }
    }

    @Test
    fun addUser_ShouldReturnSavedUser() {
        every { userRepository.save(user) } returns user

        val savedUser = usersService.addUser(user)

        assertNotNull(savedUser)
        assertEquals("Jake", savedUser.name)

        verify { userRepository.save(user) }
    }

    @Test
    fun updateUser_ShouldReturnUpdatedUser() {
        val updatedUserEntity = UserEntity(id = id, name = "Jake Smith")

        every { userRepository.existsById(id) } returns true
        every { userRepository.save(updatedUserEntity) } returns updatedUserEntity

        val user = usersService.updateUser(id, updatedUserEntity)

        assertNotNull(user)
        assertEquals("Jake Smith", user.name)

        verify { userRepository.existsById(id) }
        verify { userRepository.save(updatedUserEntity) }
    }

    @Test
    fun deleteUser_ShouldReturnTrueWhenUserExists() {
        every { userRepository.existsById(id) } returns true
        every { userRepository.deleteById(id) } just Runs

        val result = usersService.deleteUser(id)

        assertEquals(true, result)

        verify { userRepository.existsById(id) }
    }
}
