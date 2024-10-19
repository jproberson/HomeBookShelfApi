package com.example.homebookshelfapi.services.security

import com.example.homebookshelfapi.domain.dto.CustomUserDetails
import com.example.homebookshelfapi.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findByUsername(username)
            ?.let { CustomUserDetails(it) }
            ?: throw UsernameNotFoundException("Not found!")
}