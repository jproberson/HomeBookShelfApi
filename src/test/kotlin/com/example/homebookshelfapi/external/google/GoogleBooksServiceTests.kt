package com.example.homebookshelfapi.external.google

import com.example.homebookshelfapi.external.ApiEndpoints
import com.example.homebookshelfapi.domain.Book
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
        val mockBook = Book(
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
                            "title" to mockBook.title,
                            "authors" to listOf("book author"),
                            "description" to mockBook.description,
                            "categories" to listOf("Fiction"),
                            "publishedDate" to "2024-09-25",
                            "pageCount" to mockBook.pageCount,
                            "imageLinks" to mapOf("thumbnail" to mockBook.thumbnail)
                        )
                    )
                )
            )
        )

        val expectedUrl = "${ApiEndpoints.GOOGLE_BOOKS_API}${mockBook.isbn}"
        mockServer.expect(requestTo(expectedUrl))
            .andRespond(withSuccess(mockApiResponse, org.springframework.http.MediaType.APPLICATION_JSON))

        val book: Book? = googleApiService.fetchBookInfoByISBN("1234567890")

        assertNotNull(book)
        assertEquals("book title", book?.title)
        assertEquals("book author", book?.authors)
        assertEquals("a mocked book", book?.description)
        assertEquals("Fiction", book?.categories)
        assertEquals(LocalDate.of(2024, 9, 25), book?.publishedDate)
        assertEquals(100, book?.pageCount)
        assertEquals("https://example.com/thumbnail.jpg", book?.thumbnail)
    }
}
