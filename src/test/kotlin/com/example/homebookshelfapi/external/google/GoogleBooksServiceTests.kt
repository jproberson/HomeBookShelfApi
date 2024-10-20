package com.example.homebookshelfapi.external.google

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.external.ApiEndpoints
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestTemplate
import generateBookEntity

@ActiveProfiles("test")
@RestClientTest(MockGoogleApiService::class)
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

        (googleApiService as MockGoogleApiService).mockedBook = BookEntity(
            isbn = "1234567890",
            title = "Mocked Book Title",
            authors = "Mocked Author",
            description = "This is a mock book for testing purposes.",
            categories = "Fiction, Testing",
            publishedDate = LocalDate.now(),
            pageCount = 123,
            thumbnail = "http://example.com/mock-thumbnail.jpg"
        )
    }

    @Test
    fun `fetchBookInfoByISBN should return Book for valid ISBN`() {
        val mockBookEntity = generateBookEntity(isbn = "1234567890")

        val mockApiResponse = objectMapper.writeValueAsString(
            GoogleBooksResponse(
                items = listOf(
                    GoogleBookItem(
                        volumeInfo = VolumeInfo(
                            title = "Mocked Book Title",
                            authors = listOf("Mocked Author"),
                            description = "This is a mock book for testing purposes.",
                            categories = listOf("Fiction", "Testing"),
                            publishedDate = LocalDate.now().toString(),
                            pageCount = 123,
                            imageLinks = ImageLinks(thumbnail = "http://example.com/mock-thumbnail.jpg")
                        )
                    )
                )
            )
        )

        val expectedUrl = "${ApiEndpoints.GOOGLE_BOOKS_API}${mockBookEntity.isbn}"
        mockServer
            .expect(requestTo(expectedUrl))
            .andRespond(withSuccess(mockApiResponse, org.springframework.http.MediaType.APPLICATION_JSON))

        val bookEntity: BookEntity? = googleApiService.fetchBookInfoByISBN("1234567890")

        assertNotNull(bookEntity)
        assertEquals("Mocked Book Title", bookEntity.title)
        assertEquals("Mocked Author", bookEntity.authors)
        assertEquals("This is a mock book for testing purposes.", bookEntity.description)
        assertEquals("Fiction, Testing", bookEntity.categories)
        assertEquals(LocalDate.now(), bookEntity.publishedDate)
        assertEquals(123, bookEntity.pageCount)
        assertEquals("http://example.com/mock-thumbnail.jpg", bookEntity.thumbnail)
    }
}
