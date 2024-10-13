package com.example.homebookshelfapi.Integration

import com.example.homebookshelfapi.models.Users
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

@SpringBootTest(properties = ["spring.profiles.active=test"])
@AutoConfigureMockMvc
@Transactional
class UsersControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private lateinit var userJson: String

    @BeforeEach
    fun setup() {
        val userObject = Users(name = "Jake")
        userJson = ObjectMapper().writeValueAsString(userObject)
    }

    @Test
    fun addUserShouldCreateUser() {
        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Jake"))
    }

    @Test
    fun getAllUsersShouldReturnAllUsers() {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun getUserByIdShouldReturnUser() {
        val result = mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val id = JsonPath.read<String>(result.response.contentAsString, "$.id")

        mockMvc.perform(get("/api/users/$id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Jake"))
    }

    @Test
    fun updateUserShouldReturnUpdatedUser() {
        val updatedUser = Users(name = "Jake Smith")
        val updatedUserJson = ObjectMapper().writeValueAsString(updatedUser)

        val result = mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val id = JsonPath.read<String>(result.response.contentAsString, "$.id")

        mockMvc.perform(
            put("/api/users/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Jake Smith"))
    }

    @Test
    fun deleteUserShouldRemoveUser() {
        val result = mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val id = JsonPath.read<String>(result.response.contentAsString, "$.id")

        mockMvc.perform(delete("/api/users/$id"))
            .andExpect(status().isNoContent)
    }
}
