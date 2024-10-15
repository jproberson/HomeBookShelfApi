package com.example.homebookshelfapi.integration

import com.example.homebookshelfapi.domain.entities.BookEntity
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
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookApiIntegrationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private lateinit var bookJson: String

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        val bookEntity = BookEntity(
            isbn = "1234567890",
            title = "The Hobbit",
            authors = "J.R.R. Tolkien",
            description = "A fantasy novel",
            categories = "Fantasy",
            publishedDate = LocalDate.of(1937, 9, 21),
            pageCount = 310,
            thumbnail = "some_thumbnail_url"
        )
        bookJson = objectMapper.writeValueAsString(bookEntity)
    }

    @Test
    fun addBookShouldCreateBook() {
        mockMvc.perform(
            post("/v1/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value("The Hobbit"))
            .andExpect(jsonPath("$.publishedDate").value("1937-09-21"))
    }

    @Test
    fun getAllBooksShouldReturnAllBooks() {
        mockMvc.perform(get("/v1/api/books"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun getBookByIdShouldReturnBook() {
        val result = mockMvc.perform(
            post("/v1/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val bookId = JsonPath.read<String>(result.response.contentAsString, "$.id")

        mockMvc.perform(get("/v1/api/books/$bookId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("The Hobbit"))
    }

    @Test
    fun getBookByIsbnShouldReturnBook() {
        val result = mockMvc.perform(
            post("/v1/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val bookIsbn = JsonPath.read<String>(result.response.contentAsString, "$.isbn")

        mockMvc.perform(get("/v1/api/books/isbn/$bookIsbn"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("The Hobbit"))
    }

    @Test
    fun updateBookShouldReturnUpdatedBook() {
        val result = mockMvc.perform(
            post("/v1/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val bookId = JsonPath.read<String>(result.response.contentAsString, "$.id")
        val updatedBookEntity = BookEntity(
            isbn = "1234567890",
            title = "The Hobbit Updated",
            authors = "J.R.R. Tolkien",
            description = "A fantasy novel",
            categories = "Fantasy",
            publishedDate = LocalDate.of(1937, 9, 21),
            pageCount = 310,
            thumbnail = "some_thumbnail_url"
        )
        val updatedBookJson = objectMapper.writeValueAsString(updatedBookEntity)

        mockMvc.perform(
            put("/v1/api/books/$bookId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedBookJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("The Hobbit Updated"))
    }

    @Test
    fun deleteBookShouldRemoveBook() {
        val result = mockMvc.perform(
            post("/v1/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val bookId = JsonPath.read<String>(result.response.contentAsString, "$.id")

        mockMvc.perform(
            post("/v1/api/books/isbn/{isbn}", "1234567890")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated)

        mockMvc.perform(delete("/v1/api/books/$bookId"))
            .andExpect(status().isNoContent)
    }
}
