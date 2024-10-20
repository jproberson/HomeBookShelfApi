package com.example.homebookshelfapi.services.security

import com.example.homebookshelfapi.config.security.JwtProperties
import com.example.homebookshelfapi.controllers.security.AuthenticationRequest
import com.example.homebookshelfapi.controllers.security.AuthenticationResponse
import com.example.homebookshelfapi.repositories.security.RefreshTokenRepository
import java.util.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
  private val authManager: AuthenticationManager,
  private val userDetailsService: CustomUserDetailsService,
  private val tokenService: TokenService,
  private val jwtProperties: JwtProperties,
  private val refreshTokenRepository: RefreshTokenRepository,
) {

  fun authentication(authenticationRequest: AuthenticationRequest): AuthenticationResponse {
    authManager.authenticate(
      UsernamePasswordAuthenticationToken(
        authenticationRequest.username,
        authenticationRequest.password
      )
    )

    val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username)
    val accessToken = createAccessToken(userDetails)

    val activeTokens = tokenService.findActiveTokens(userDetails)
    val refreshToken =
      if (activeTokens.isNotEmpty()) {
        activeTokens.first().token
      } else {
        val newRefreshToken = createRefreshToken(userDetails)
        tokenService.revokeExistingTokens(userDetails)
        tokenService.saveRefreshToken(newRefreshToken, userDetails, getRefreshTokenExpiration())
        newRefreshToken
      }
    return AuthenticationResponse(accessToken = accessToken, refreshToken = refreshToken)
  }

  fun refreshAccessToken(refreshToken: String): String? {
    val extractedUsername = tokenService.extractUsername(refreshToken)

    return extractedUsername?.let { username ->
      val currentUserDetails = userDetailsService.loadUserByUsername(username)
      val refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)

      if (
        refreshTokenEntity?.token?.isNotEmpty() == true &&
          tokenService.isValid(refreshTokenEntity.token) &&
          refreshTokenEntity.user.username == currentUserDetails.username
      )
        createAccessToken(currentUserDetails)
      else null
    }
  }

  private fun createAccessToken(user: UserDetails) =
    tokenService.generate(userDetails = user, expirationDate = getAccessTokenExpiration())

  private fun createRefreshToken(user: UserDetails) =
    tokenService.generate(userDetails = user, expirationDate = getRefreshTokenExpiration())

  private fun getAccessTokenExpiration(): Date =
    Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)

  private fun getRefreshTokenExpiration(): Date =
    Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
}
