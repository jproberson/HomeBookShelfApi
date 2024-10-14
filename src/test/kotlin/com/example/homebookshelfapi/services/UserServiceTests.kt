package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.repositories.UserRepository
import com.example.homebookshelfapi.services.impl.UsersServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserEntityServiceTests {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var usersService: UsersServiceImpl

    private lateinit var user: UserEntity

    private lateinit var id: UUID

    @BeforeEach
    fun setup() {
        id = UUID.randomUUID()
        MockitoAnnotations.openMocks(this)
        user = UserEntity(id = id, name = "Jake")
    }

    @Test
    fun getAllUsers_ShouldReturnAllUsers() {
        `when`(userRepository.findAll()).thenReturn(listOf(user))
        val users = usersService.getAllUsers()
        assertNotNull(users)
        assertEquals(1, users.size)
        assertEquals("Jake", users[0].name)
    }

    @Test
    fun getUserById_ShouldReturnUserWhenFound() {
        `when`(userRepository.findById(user.id)).thenReturn(Optional.of(user))
        val foundUser = usersService.getUserById(user.id)
        assertNotNull(foundUser)
        assertEquals("Jake", foundUser.name)
    }

    @Test
    fun addUser_ShouldReturnSavedUser() {
        `when`(userRepository.save(user)).thenReturn(user)
        val savedUser = usersService.addUser(user)
        assertNotNull(savedUser)
        assertEquals("Jake", savedUser.name)
    }


    @Test
    fun updateUser_ShouldReturnUpdatedUser() {
        val updatedUserEntity = UserEntity(id = id, name = "Jake Smith")
        `when`(userRepository.existsById(id)).thenReturn(true)
        `when`(userRepository.save(updatedUserEntity)).thenReturn(updatedUserEntity)
        val user = usersService.updateUser(id, updatedUserEntity)
        assertNotNull(user)
        assertEquals("Jake Smith", user.name)
    }

    @Test
    fun deleteUser_ShouldReturnTrueWhenUserExists() {
        `when`(userRepository.existsById(id)).thenReturn(true)
        val result = usersService.deleteUser(id)
        assertEquals(true, result)
    }
}