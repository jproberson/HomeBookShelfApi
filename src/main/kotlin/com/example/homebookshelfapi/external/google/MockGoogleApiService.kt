package com.example.homebookshelfapi.external.google

import com.example.homebookshelfapi.domain.entities.BookEntity
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Profile("test")
@Service
class MockGoogleApiService : GoogleApiService {
    override fun fetchBookInfoByISBN(isbn: String): BookEntity? {
        return BookEntity(
            id = UUID.randomUUID(),
            isbn = isbn,
            title = "Mocked Book Title",
            authors = "Mocked Author",
            description = "This is a mock book for testing purposes.",
            categories = "Fiction, Testing",
            publishedDate = LocalDate.now(),
            pageCount = 123,
            thumbnail = "http://example.com/mock-thumbnail.jpg"
        )
    }
}
