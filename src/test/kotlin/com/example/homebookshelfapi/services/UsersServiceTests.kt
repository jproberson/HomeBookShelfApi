package com.example.homebookshelfapi.services

import com.example.homebookshelfapi.domain.Users
import com.example.homebookshelfapi.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UsersServiceTests {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var usersService: UsersService

    private lateinit var users: Users

    private lateinit var id: UUID

    @BeforeEach
    fun setup() {
        id = UUID.randomUUID()
        MockitoAnnotations.openMocks(this)
        users = Users(id = id, name = "Jake")
    }

    @Test
    fun getAllUsers_ShouldReturnAllUsers() {
        `when`(userRepository.findAll()).thenReturn(listOf(users))
        val users = usersService.getAllUsers()
        assertNotNull(users)
        assertEquals(1, users.size)
        assertEquals("Jake", users[0].name)
    }

    @Test
    fun getUserById_ShouldReturnUserWhenFound() {
        `when`(userRepository.findById(users.id)).thenReturn(Optional.of(users))
        val foundUser = usersService.getUserById(users.id)
        assertNotNull(foundUser)
        assertEquals("Jake", foundUser.name)
    }

    @Test
    fun addUser_ShouldReturnSavedUser() {
        `when`(userRepository.save(users)).thenReturn(users)
        val savedUser = usersService.addUser(users)
        assertNotNull(savedUser)
        assertEquals("Jake", savedUser.name)
    }


    @Test
    fun updateUser_ShouldReturnUpdatedUser() {
        val updatedUsers = Users(id = id, name = "Jake Smith")
        `when`(userRepository.existsById(id)).thenReturn(true)
        `when`(userRepository.save(updatedUsers)).thenReturn(updatedUsers)
        val user = usersService.updateUser(id, updatedUsers)
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