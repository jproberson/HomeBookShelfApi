package com.example.homebookshelfapi.controllers.admin

import com.example.homebookshelfapi.domain.dto.UserDto
import com.example.homebookshelfapi.domain.dto.UserRequest
import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.services.UsersService
import com.example.homebookshelfapi.toUserDto
import com.example.homebookshelfapi.toUserEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/api/user")
class UsersController(private val usersService: UsersService) {
    @GetMapping
    fun getAllUsers() = usersService.getAllUsers()

    @PostMapping
    fun addUser(@RequestBody user: UserRequest): ResponseEntity<UserDto> {
        val newUser = usersService.addUser(user.toUserEntity())
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser.toUserDto())
    }

    @GetMapping("/{username}")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<UserEntity> {
        val user = usersService.getByUsername(username)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{username}")
    fun deleteUser(@PathVariable username: String): ResponseEntity<Void> {
        return if (usersService.deleteUser(username)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
