package com.example.homebookshelfapi.Integration

import com.example.homebookshelfapi.models.Book
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
class BookControllerIntegrationTest {
    // TODO: Switch to using Test Containers?

    @Autowired
    private lateinit var mockMvc: MockMvc

    private lateinit var bookJson: String

    @BeforeEach
    fun setup() {
        val book = Book(
            isbn = "1234567890",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            publishedYear = 1937
        )
        bookJson = ObjectMapper().writeValueAsString(book)
    }

    @Test
    fun addBookShouldCreateBook() {
        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value("The Hobbit"))
    }

    @Test
    fun getAllBooksShouldReturnAllBooks() {
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun getBookByIdShouldReturnBook() {
        val result = mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val bookId = JsonPath.read<String>(result.response.contentAsString, "$.id")

        mockMvc.perform(get("/api/books/$bookId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("The Hobbit"))
    }

    @Test
    fun getBookByIsbnShouldReturnBook() {
        val result = mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val bookIsbn = JsonPath.read<String>(result.response.contentAsString, "$.isbn")

        mockMvc.perform(get("/api/books/isbn/$bookIsbn"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("The Hobbit"))
    }

    @Test
    fun updateBookShouldReturnUpdatedBook() {
        val result = mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val bookId = JsonPath.read<String>(result.response.contentAsString, "$.id")
        val updatedBook = Book(
            isbn = "1234567890",
            title = "The Hobbit Updated",
            author = "J.R.R. Tolkien",
            publishedYear = 1937
        )
        val updatedBookJson = ObjectMapper().writeValueAsString(updatedBook)

        mockMvc.perform(
            put("/api/books/$bookId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedBookJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("The Hobbit Updated"))
    }

    @Test
    fun deleteBookShouldRemoveBook() {
        val result = mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val bookId = JsonPath.read<String>(result.response.contentAsString, "$.id")

        mockMvc.perform(delete("/api/books/$bookId"))
            .andExpect(status().isNoContent)
    }
}
