package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.Role
import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.repositories.UserRepository
import com.example.homebookshelfapi.services.impl.UsersServiceImpl
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserEntityServiceTests {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var encoder: PasswordEncoder

    @InjectMockKs
    private lateinit var usersService: UsersServiceImpl

    private lateinit var user: UserEntity

    private lateinit var id: UUID

    @BeforeEach
    fun setup() {
        id = UUID.randomUUID()
        MockKAnnotations.init(this)
        user = UserEntity(id = id, username = "Jake", password = "password")
    }

    @Test
    fun getAllUsers_ShouldReturnAllUsers() {
        every { userRepository.findAll() } returns listOf(user)

        val users = usersService.getAllUsers()

        assertNotNull(users)
        assertEquals(1, users.size)
        assertEquals("Jake", users[0].username)

        verify { userRepository.findAll() }
    }

    @Test
    fun getByUsername_ShouldReturnUserWhenFound() {
        every { userRepository.findByUsername(user.username) } returns user

        val foundUser = usersService.getByUsername(user.username)

        assertNotNull(foundUser)
        assertEquals("Jake", foundUser.username)

        verify { userRepository.findByUsername(user.username) }
    }

    @Test
    fun addUser_ShouldReturnSavedUser() {
        every { encoder.encode(user.password) } returns "encodedPassword123"
        every { userRepository.save(any()) } answers { firstArg() }

        val savedUser = usersService.addUser(user)

        assertNotNull(savedUser)
        assertEquals("Jake", savedUser.username)
        assertEquals("encodedPassword123", savedUser.password)
        assertEquals(Role.USER, savedUser.role)
        assertEquals(true, savedUser.enabled)

        verify { encoder.encode(user.password) }
        verify { userRepository.save(any()) }
    }

    @Test
    fun deleteUser_ShouldReturnTrueWhenUserExists() {
        every { userRepository.findByUsername(user.username) } returns user
        every { userRepository.deleteByUsername(user.username) } just Runs

        val result = usersService.deleteUser(user.username)

        assertEquals(true, result)
        verify { userRepository.findByUsername(user.username) }
        verify { userRepository.deleteByUsername(user.username) }
    }

    @Test
    fun deleteUser_ShouldReturnFalseWhenUserDoesNotExist() {
        every { userRepository.findByUsername(user.username) } returns null

        val result = usersService.deleteUser(user.username)

        assertEquals(false, result)

        verify { userRepository.findByUsername(user.username) }
        verify(exactly = 0) { userRepository.deleteByUsername(any()) }
    }
}
