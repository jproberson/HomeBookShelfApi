package com.example.homebookshelfapi.integration

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
import testUserEntity

private const val USERS_BASE_URL = "/v1/api/users"

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsersApiIntegrationTest {

  @Autowired private lateinit var mockMvc: MockMvc

  private lateinit var userJson: String

  @BeforeEach
  fun setup() {
    val user = testUserEntity()
    userJson = ObjectMapper().writeValueAsString(user)
  }

  @Test
  fun addUserShouldCreateUser() {
    mockMvc
      .perform(
        post(USERS_BASE_URL)
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .content(userJson)
      )
      .andExpect(status().isCreated)
      .andExpect(jsonPath("$.name").value("Jake"))
  }

  @Test
  fun getAllUsersShouldReturnAllUsers() {
    mockMvc.perform(get(USERS_BASE_URL)).andExpect(status().isOk).andExpect(jsonPath("$").isArray)
  }

  @Test
  fun getUserByIdShouldReturnUser() {
    val result =
      mockMvc
        .perform(
          post(USERS_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(userJson)
        )
        .andExpect(status().isCreated)
        .andReturn()

    val id = JsonPath.read<String>(result.response.contentAsString, "$.id")

    mockMvc
      .perform(get("$USERS_BASE_URL/$id"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.name").value("Jake"))
  }

  @Test
  fun deleteUserShouldRemoveUser() {
    val result =
      mockMvc
        .perform(
          post(USERS_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(userJson)
        )
        .andExpect(status().isCreated)
        .andReturn()

    val id = JsonPath.read<String>(result.response.contentAsString, "$.id")

    mockMvc.perform(delete("$USERS_BASE_URL/$id")).andExpect(status().isNoContent)
  }
}
