package com.example.homebookshelfapi.integration

import com.example.homebookshelfapi.domain.entities.UserEntity
import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsersApiIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private lateinit var userJson: String

    @BeforeEach
    fun setup() {
        val user = UserEntity(name = "Jake")
        userJson = ObjectMapper().writeValueAsString(user)
    }

    @Test
    fun addUserShouldCreateUser() {
        mockMvc.perform(
            post("/v1/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Jake"))
    }

    @Test
    fun getAllUsersShouldReturnAllUsers() {
        mockMvc.perform(get("/v1/api/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun getUserByIdShouldReturnUser() {
        val result = mockMvc.perform(
            post("/v1/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val id = JsonPath.read<String>(result.response.contentAsString, "$.id")

        mockMvc.perform(get("/v1/api/users/$id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Jake"))
    }

    @Test
    fun updateUserShouldReturnUpdatedUser() {
        val updatedUserEntity = UserEntity(name = "Jake Smith")
        val updatedUserJson = ObjectMapper().writeValueAsString(updatedUserEntity)

        val result = mockMvc.perform(
            post("/v1/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val id = JsonPath.read<String>(result.response.contentAsString, "$.id")

        mockMvc.perform(
            put("/v1/api/users/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Jake Smith"))
    }

    @Test
    fun deleteUserShouldRemoveUser() {
        val result = mockMvc.perform(
            post("/v1/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val id = JsonPath.read<String>(result.response.contentAsString, "$.id")

        mockMvc.perform(delete("/v1/api/users/$id"))
            .andExpect(status().isNoContent)
    }
}
