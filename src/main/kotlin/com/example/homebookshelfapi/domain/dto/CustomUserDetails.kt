package com.example.homebookshelfapi.domain.dto

import com.example.homebookshelfapi.domain.entities.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val userEntity: UserEntity
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${userEntity.role}"))


    override fun getPassword(): String = userEntity.password

    override fun getUsername(): String = userEntity.username

    fun getUserEntity(): UserEntity = userEntity

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}
