package com.example.homebookshelfapi.controllers.admin

import com.example.homebookshelfapi.controllers.BaseIntegrationTest
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

private const val USERS_BASE_URL = "/v1/api/user"

@SpringBootTest
@AutoConfigureMockMvc
class UsersApiIntegrationTest : BaseIntegrationTest() {
    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `getAllUsers should return all users for admins`() {
        mockMvc
            .perform(get(USERS_BASE_URL).with(csrf()))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2)) // seeding 2 users
    }

    @Test
    @Transactional
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `getAllUsers should return forbidden for non-admin`() {
        mockMvc
            .perform(get(USERS_BASE_URL).with(csrf()))
            .andExpect(status().isForbidden)
    }

    @ParameterizedTest
    @Transactional
    @ValueSource(strings = ["USER", "ADMIN"])
    fun `addUser should return created for user and admins`(role: String) {
        mockMvc
            .perform(
                post(USERS_BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "username": "newUser",
                        "password": "password"
                    }
                    """.trimIndent()
                    )
                    .with(csrf())
                    .with(user("newUser").roles(role))
            )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value("newUser"))
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `getUserByUsername should return user for admins`() {
        mockMvc
            .perform(get("$USERS_BASE_URL/admin").with(csrf()))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value("admin"))
    }

    @Test
    @Transactional
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `getUserByUsername should return forbidden for non-admin`() {
        mockMvc
            .perform(get("$USERS_BASE_URL/testuser").with(csrf()))
            .andExpect(status().isForbidden)
    }


    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `deleteUser should return no content for admins`() {
        mockMvc
            .perform(delete("$USERS_BASE_URL/admin").with(csrf()))
            .andExpect(status().isNoContent)
    }

    @Test
    @Transactional
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `deleteUser should return forbidden for non-admin`() {
        mockMvc
            .perform(delete("$USERS_BASE_URL/testuser").with(csrf()))
            .andExpect(status().isForbidden)
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `deleteUser should return not found for non-existing user`() {
        mockMvc
            .perform(get("$USERS_BASE_URL/nonExistingUser").with(csrf()))
            .andExpect(status().isNotFound)
    }

}

