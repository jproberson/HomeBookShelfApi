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
import testBookEntity

@ActiveProfiles("test")
@RestClientTest(GoogleApiService::class)
class GoogleApiServiceTest {

  @Autowired private lateinit var googleApiService: GoogleApiService

  @Autowired private lateinit var restTemplate: RestTemplate

  private lateinit var mockServer: MockRestServiceServer
  private lateinit var objectMapper: ObjectMapper

  @BeforeEach
  fun setup() {
    mockServer = MockRestServiceServer.createServer(restTemplate)
    objectMapper = ObjectMapper()
  }

  @Test
  fun `fetchBookInfoByISBN should return Book for valid ISBN`() {
    val mockBookEntity = testBookEntity(isbn = "1234567890")

    val mockApiResponse =
      objectMapper.writeValueAsString(
        mapOf(
          "items" to
            listOf(
              mapOf(
                "volumeInfo" to
                  mapOf(
                    "title" to "Mocked Book Title",
                    "authors" to "Mocked Author",
                    "description" to "This is a mock book for testing purposes.",
                    "categories" to listOf("Fiction", "Testing"),
                    "publishedDate" to LocalDate.now().toString(),
                    "pageCount" to 123,
                    "imageLinks" to "http://example.com/mock-thumbnail.jpg"
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
