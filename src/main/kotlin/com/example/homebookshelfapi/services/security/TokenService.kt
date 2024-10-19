package com.example.homebookshelfapi.services.security

import com.example.homebookshelfapi.config.security.JwtProperties
import com.example.homebookshelfapi.domain.dto.CustomUserDetails
import com.example.homebookshelfapi.domain.entities.RefreshTokenEntity
import com.example.homebookshelfapi.repositories.security.RefreshTokenRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService(
    jwtProperties: JwtProperties,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    private val secretKey = Keys.hmacShaKeyFor(
        jwtProperties.key.toByteArray()
    )

    fun generate(
        userDetails: UserDetails,
        expirationDate: Date,
        additionalClaims: Map<String, Any> = emptyMap()
    ): String =
        Jwts.builder()
            .claims()
            .subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(expirationDate)
            .add(additionalClaims)
            .and()
            .signWith(secretKey)
            .compact()

    fun saveRefreshToken(token: String, userDetails: UserDetails, expirationDate: Date) {
        val userEntity = (userDetails as CustomUserDetails).getUserEntity()
        val refreshTokenEntity = RefreshTokenEntity(
            token = token,
            user = userEntity,
            expirationDate = expirationDate
        )
        refreshTokenRepository.save(refreshTokenEntity)
    }

    fun findActiveTokens(userDetails: UserDetails): List<RefreshTokenEntity> {
        val userEntity = (userDetails as CustomUserDetails).getUserEntity()
        return refreshTokenRepository.findActiveTokensByUser(userEntity)
    }

    fun revokeExistingTokens(userDetails: UserDetails) {
        val userEntity = (userDetails as CustomUserDetails).getUserEntity()
        val activeTokens = refreshTokenRepository.findActiveTokensByUser(userEntity)
        activeTokens.forEach { token ->
            val revokedToken = token.copy(revoked = true)
            refreshTokenRepository.save(revokedToken)
        }
    }

    fun extractUsername(token: String): String? =
        getAllClaims(token)
            .subject

    fun isValid(token: String): Boolean {
        return getAllClaims(token)
            .expiration
            .after(Date(System.currentTimeMillis()))
    }

    private fun getAllClaims(token: String): Claims {
        val parser = Jwts.parser()
            .verifyWith(secretKey)
            .build()

        return parser
            .parseSignedClaims(token)
            .payload
    }
}