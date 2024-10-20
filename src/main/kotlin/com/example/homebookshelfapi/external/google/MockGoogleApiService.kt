package com.example.homebookshelfapi.external.google

import com.example.homebookshelfapi.domain.entities.BookEntity
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import generateBookEntity

@Profile("test", "integration")
@Service
class MockGoogleApiService : GoogleApiService {
    var mockedBook: BookEntity? = null

    override fun fetchBookInfoByISBN(isbn: String): BookEntity? {
        return mockedBook ?: generateBookEntity(isbn = isbn)
    }
}
