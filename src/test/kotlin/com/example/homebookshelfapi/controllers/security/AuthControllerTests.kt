package com.example.homebookshelfapi.controllers.security

import com.example.homebookshelfapi.controllers.BaseIntegrationTest
import com.example.homebookshelfapi.repositories.security.RefreshTokenRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import kotlin.test.Test

private const val AUTH_BASE_URL = "/v1/api/auth"

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTests : BaseIntegrationTest() {

    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Test
    @Transactional
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `authenticate should return a token for user`() {
        mockMvc
            .perform(
                post(AUTH_BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "username": "testuser",
                        "password": "password" 
                     }
                    """.trimIndent()
                    )
                    .with(csrf())
                    .with(user("testuser").roles("USER"))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accessToken").isString)
            .andExpect(jsonPath("$.refreshToken").isString)
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `authenticate should return a token for admin`() {
        mockMvc
            .perform(
                post(AUTH_BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "username": "admin",
                        "password": "password" 
                     }
                    """.trimIndent()
                    )
                    .with(csrf())
                    .with(user("testuser").roles("USER"))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accessToken").isString)
            .andExpect(jsonPath("$.refreshToken").isString)
    }

    @Test
    @Transactional
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `refreshAccessToken should return a token for user`() {
        // Authenticate user
        mockMvc
            .perform(
                post(AUTH_BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "username": "testuser",
                        "password": "password"
                     }
                    """.trimIndent()
                    )
                    .with(csrf())
                    .with(user("testuser").roles("USER"))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accessToken").isString)
            .andExpect(jsonPath("$.refreshToken").isString)

        val user = userRepository.findByUsername("testuser")
        val activeTokens = refreshTokenRepository.findActiveTokensByUser(user!!)
        val token = activeTokens.firstOrNull()?.token

        // Refresh token
        mockMvc
            .perform(
                post("$AUTH_BASE_URL/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "token": "$token"
                        }
                        """.trimIndent()
                    )
                    .with(csrf())
                    .with(user("testuser").roles("USER"))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.token").isString)
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `refreshAccessToken should return a token for admin`() {
        // Authenticate admin
        mockMvc
            .perform(
                post(AUTH_BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "username": "admin",
                        "password": "password"
                     }
                    """.trimIndent()
                    )
                    .with(csrf())
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accessToken").isString)
            .andExpect(jsonPath("$.refreshToken").isString)

        val user = userRepository.findByUsername("admin")
        val activeTokens = refreshTokenRepository.findActiveTokensByUser(user!!)
        val token = activeTokens.firstOrNull()?.token

        // Refresh token
        mockMvc
            .perform(
                post("$AUTH_BASE_URL/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "token": "$token"
                        }
                        """.trimIndent()
                    )
                    .with(csrf())
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.token").isString)
    }
}