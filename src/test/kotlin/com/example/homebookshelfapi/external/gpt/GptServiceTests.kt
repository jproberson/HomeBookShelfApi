import com.example.homebookshelfapi.external.gpt.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GptServiceTests {

  @MockK private lateinit var gptRequestBuilder: GptRequestBuilder

  @MockK private lateinit var gptApiClient: GptApiClient

  @InjectMockKs private lateinit var gptServiceImpl: GptServiceImpl

  @Test
  fun `get book recommendation should return a list of ISBNs`() {
    val storedBooks = listOf("Book 1", "Book 2")
    val requestPayload =
      GptRequest(
        model = "gpt-4o-mini",
        messages =
          listOf(
            GptMessage("system", "Provide a list of book ISBNs."),
            GptMessage("user", "I have the following books in my collection: Book 1, Book 2.")
          )
      )
    val gptResponse =
      GptResponse(
        choices = listOf(GptChoice(GptMessage("assistant", "978-1234567890, 978-0987654321")))
      )

    every { gptRequestBuilder.buildBookRecommendationsRequest(storedBooks) } returns requestPayload
    every { gptApiClient.postGptRequest(requestPayload) } returns gptResponse

    val bookRecommendations = gptServiceImpl.getBookRecommendations(storedBooks)

    assertNotNull(bookRecommendations)
  }

  @Test
  fun `get book recommendation should throw an error when given an empty list`() {
    val storedBooks = emptyList<String>()

    assertThrows<IllegalArgumentException> { gptServiceImpl.getBookRecommendations(storedBooks) }
  }
}
