package com.example.homebookshelfapi.controllers

import com.example.homebookshelfapi.domain.User
import com.example.homebookshelfapi.services.UsersService
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
    fun getUserById(@PathVariable id: UUID): ResponseEntity<User> {
        val user = usersService.getUserById(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun addUser(@RequestBody user: User): ResponseEntity<User> {
        val newUser = usersService.addUser(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody updatedUser: User): ResponseEntity<User> {
        val user = usersService.updateUser(id, updatedUser)
        return if (user != null) {
            ResponseEntity.ok(user)
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