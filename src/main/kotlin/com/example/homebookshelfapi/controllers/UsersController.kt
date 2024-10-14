package com.example.homebookshelfapi.controllers

import com.example.homebookshelfapi.domain.dto.UserDto
import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.services.UsersService
import com.example.homebookshelfapi.toUserDto
import com.example.homebookshelfapi.toUserEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/v1/api/users")
class UsersController(private val usersService: UsersService) {
    @GetMapping
    fun getAllUsers() = usersService.getAllUsers()

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<UserEntity> {
        val user = usersService.getUserById(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun addUser(@RequestBody user: UserDto): ResponseEntity<UserDto> {
        val newUser = usersService.addUser(user.toUserEntity())
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser.toUserDto())
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody updatedUser: UserDto): ResponseEntity<UserDto> {
        val user = usersService.updateUser(id, updatedUser.toUserEntity())
        return if (user != null) {
            ResponseEntity.ok(user.toUserDto())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (usersService.deleteUser(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}