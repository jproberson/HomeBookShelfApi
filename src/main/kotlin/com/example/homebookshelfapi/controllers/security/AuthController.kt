package com.example.homebookshelfapi.controllers.security

import com.example.homebookshelfapi.services.security.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

data class AuthenticationRequest(
    val username: String,
    val password: String,
)

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
)

data class RefreshTokenRequest(val token: String)

data class TokenResponse(val token: String)

@RestController
@RequestMapping("/v1/api/auth")
class AuthController(
    private val authenticationService: AuthenticationService,
) {
    @PostMapping
    fun authenticate(@RequestBody authRequest: AuthenticationRequest): AuthenticationResponse =
        authenticationService.authentication(authRequest)

    @PostMapping("/refresh")
    fun refreshAccessToken(@RequestBody request: RefreshTokenRequest): TokenResponse =
        authenticationService.refreshAccessToken(request.token)?.mapToTokenResponse()
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token.")

    private fun String.mapToTokenResponse(): TokenResponse = TokenResponse(token = this)
}
