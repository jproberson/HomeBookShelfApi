package com.example.homebookshelfapi.controllers

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.external.google.GoogleApiService
import com.example.homebookshelfapi.external.google.MockGoogleApiService
import com.example.homebookshelfapi.setup.BaseIntegrationTest
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import generateBookEntity
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

private const val BOOKS_BASE_URL = "/v1/api/books"

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIntegrationTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var googleApiService: GoogleApiService

    var username = "testuser"

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `getAllBooks should return all books`() {
        mockMvc
            .perform(get(BOOKS_BASE_URL).with(csrf()))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(3)) // seeding 3 books
    }

    @ParameterizedTest
    @ValueSource(strings = ["USER", "ADMIN"])
    fun `should return all books for the authenticated user`(role: String) {
        mockMvc
            .perform(
                get("$BOOKS_BASE_URL/user/$username")
                    .with(user("testuser").roles(role)).with(csrf())
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(0))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should return a book by id`() {
        val result =
            mockMvc
                .perform(get(BOOKS_BASE_URL).with(csrf()))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()

        val booksJson = result.response.contentAsString
        val books: List<BookEntity> =
            ObjectMapper().readValue(booksJson, object : TypeReference<List<BookEntity>>() {})

        assert(books.isNotEmpty())

        val firstBookId = books[0].id

        mockMvc
            .perform(get("$BOOKS_BASE_URL/$firstBookId").with(csrf()))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(firstBookId.toString()))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should update a book by id`() {
        val result =
            mockMvc
                .perform(get(BOOKS_BASE_URL).with(csrf()))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()

        val booksJson = result.response.contentAsString
        val books: List<BookEntity> =
            ObjectMapper().readValue(booksJson, object : TypeReference<List<BookEntity>>() {})

        assert(books.isNotEmpty())

        val firstBookId = books[0].id
        val updatedBook = books[0].copy(title = "Updated Title")

        mockMvc
            .perform(
                put("$BOOKS_BASE_URL/$firstBookId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapper().writeValueAsString(updatedBook))
                    .with(csrf())
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title").value("Updated Title"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should return a book by isbn`() {
        val result =
            mockMvc
                .perform(get(BOOKS_BASE_URL).with(csrf()))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()

        val booksJson = result.response.contentAsString
        val books: List<BookEntity> =
            ObjectMapper().readValue(booksJson, object : TypeReference<List<BookEntity>>() {})

        assert(books.isNotEmpty())

        val firstBookIsbn = books[0].isbn

        mockMvc
            .perform(get("$BOOKS_BASE_URL/isbn/$firstBookIsbn").with(csrf()))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.isbn").value(firstBookIsbn))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should add a book by isbn to overall books and user books`() {
        val newBook = generateBookEntity()
        (googleApiService as MockGoogleApiService).mockedBook = newBook

        mockMvc
            .perform(
                post("$BOOKS_BASE_URL/isbn/${newBook.isbn}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
            )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title").value(newBook.title))

        // check if the book was added to all books
        mockMvc
            .perform(get("$BOOKS_BASE_URL/isbn/${newBook.isbn}").with(csrf()))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.isbn").value(newBook.isbn))

        // check if the book was added to the user's books
        mockMvc
            .perform(get("$BOOKS_BASE_URL/user/$username").with(csrf()))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].isbn").value(newBook.isbn))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should delete a book by id`() {
        val result =
            mockMvc
                .perform(get(BOOKS_BASE_URL).with(csrf()))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()

        val booksJson = result.response.contentAsString
        val books: List<BookEntity> =
            ObjectMapper().readValue(booksJson, object : TypeReference<List<BookEntity>>() {})

        assert(books.isNotEmpty())

        val firstBookIsbn = books[0].isbn
        val firstBookId = books[0].id

        mockMvc
            .perform(
                post("$BOOKS_BASE_URL/isbn/${firstBookIsbn}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
            )
            .andExpect(status().isCreated)

        mockMvc
            .perform(delete("$BOOKS_BASE_URL/$firstBookId").with(csrf()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(firstBookId.toString()))

    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should not delete a book by id if user is not the owner`() {
        val result =
            mockMvc
                .perform(get(BOOKS_BASE_URL).with(csrf()))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()

        val booksJson = result.response.contentAsString
        val books: List<BookEntity> =
            ObjectMapper().readValue(booksJson, object : TypeReference<List<BookEntity>>() {})

        assert(books.isNotEmpty())

        val bookId = books[0].id

        mockMvc
            .perform(delete("$BOOKS_BASE_URL/$bookId").with(csrf()))
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(username = "not-a-user", roles = ["USER"])
    fun `deleteBook should return 404 if user is not found`() {
        val result =
            mockMvc
                .perform(get(BOOKS_BASE_URL).with(csrf()))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()

        val booksJson = result.response.contentAsString
        val books: List<BookEntity> =
            ObjectMapper().readValue(booksJson, object : TypeReference<List<BookEntity>>() {})

        assert(books.isNotEmpty())

        val firstBookId = books[0].id

        mockMvc
            .perform(delete("$BOOKS_BASE_URL/$firstBookId").with(csrf()))
            .andExpect(status().isNotFound)
    }

//    @Test
//    @WithMockUser(username = "testuser", roles = ["USER"])
//    fun `should get recommended books`() {
//        mockMvc
//            .perform(get("BOOKS_BASE_URL/recommended").with(csrf()))
//            .andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(jsonPath("$.length()").value(0))
//    }
}