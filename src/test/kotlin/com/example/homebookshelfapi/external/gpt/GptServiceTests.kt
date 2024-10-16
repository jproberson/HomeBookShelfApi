import com.example.homebookshelfapi.external.gpt.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import kotlin.test.assertNotNull

@ExtendWith(MockKExtension::class)
class GptServiceTests {

    @MockK
    private lateinit var gptRequestBuilder: GptRequestBuilder

    @MockK
    private lateinit var gptApiClient: GptApiClient

    @InjectMockKs
    private lateinit var gptServiceImpl: GptServiceImpl

    @Test
    fun `get book recommendation should return a list of ISBNs`() {
        val storedBooks = listOf("Book 1", "Book 2")
        val requestPayload = mapOf("key" to "value")
        val gptResponse = GptResponse(
            choices = listOf(
                GptChoice(
                    GptMessage("978-1234567890, 978-0987654321")
                )
            )
        )

        every { gptRequestBuilder.buildBookRecommendationsRequest(storedBooks) } returns requestPayload
        every { gptApiClient.postGptRequest(requestPayload) } returns Mono.just(gptResponse)

        val bookRecommendations = gptServiceImpl.getBookRecommendations(storedBooks).block()

        assertNotNull(bookRecommendations)
    }

    @Test
    fun `get book recommendation should throw an error when given an empty list`() {
        val storedBooks = emptyList<String>()

        assertThrows<IllegalArgumentException> {
            gptServiceImpl.getBookRecommendations(storedBooks).block()
        }
    }
}
