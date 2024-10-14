package com.example.homebookshelfapi.external.google

import com.example.homebookshelfapi.external.ApiEndpoints
import com.example.homebookshelfapi.domain.entities.BookEntity
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import java.util.*

@RestClientTest(GoogleApiService::class)
class GoogleApiServiceTest {

    @Autowired
    private lateinit var googleApiService: GoogleApiService

    @Autowired
    private lateinit var restTemplate: RestTemplate

    private lateinit var mockServer: MockRestServiceServer
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
        objectMapper = ObjectMapper()
    }

    @Test
    fun `fetchBookInfoByISBN should return Book for valid ISBN`() {
        val mockBookEntity = BookEntity(
            id = UUID.randomUUID(),
            isbn = "1234567890",
            title = "book title",
            authors = "book author",
            description = "a mocked book",
            categories = "Fiction",
            publishedDate = LocalDate.of(2024, 9, 25),
            pageCount = 100,
            thumbnail = "https://example.com/thumbnail.jpg"
        )

        val mockApiResponse = objectMapper.writeValueAsString(
            mapOf(
                "items" to listOf(
                    mapOf(
                        "volumeInfo" to mapOf(
                            "title" to mockBookEntity.title,
                            "authors" to listOf("book author"),
                            "description" to mockBookEntity.description,
                            "categories" to listOf("Fiction"),
                            "publishedDate" to "2024-09-25",
                            "pageCount" to mockBookEntity.pageCount,
                            "imageLinks" to mapOf("thumbnail" to mockBookEntity.thumbnail)
                        )
                    )
                )
            )
        )

        val expectedUrl = "${ApiEndpoints.GOOGLE_BOOKS_API}${mockBookEntity.isbn}"
        mockServer.expect(requestTo(expectedUrl))
            .andRespond(withSuccess(mockApiResponse, org.springframework.http.MediaType.APPLICATION_JSON))

        val bookEntity: BookEntity? = googleApiService.fetchBookInfoByISBN("1234567890")

        assertNotNull(bookEntity)
        assertEquals("book title", bookEntity?.title)
        assertEquals("book author", bookEntity?.authors)
        assertEquals("a mocked book", bookEntity?.description)
        assertEquals("Fiction", bookEntity?.categories)
        assertEquals(LocalDate.of(2024, 9, 25), bookEntity?.publishedDate)
        assertEquals(100, bookEntity?.pageCount)
        assertEquals("https://example.com/thumbnail.jpg", bookEntity?.thumbnail)
    }
}
