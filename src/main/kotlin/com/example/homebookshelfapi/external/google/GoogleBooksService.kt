package com.example.homebookshelfapi.external.google

import com.example.homebookshelfapi.external.ApiEndpoints
import com.example.homebookshelfapi.domain.Book
import com.example.homebookshelfapi.utils.DateParserUtil
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.util.*

@Service
class GoogleApiService(private val restTemplate: RestTemplate) {

    fun fetchBookInfoByISBN(isbn: String): Book? {
        val url = "${ApiEndpoints.GOOGLE_BOOKS_API}$isbn"
        val response = restTemplate.getForObject<GoogleBooksResponse>(url)

        val volumeInfo = response.items?.firstOrNull()?.volumeInfo ?: return null
        val publishedDate = DateParserUtil.parseDate(volumeInfo.publishedDate)

        return Book(
            id = UUID.randomUUID(),
            isbn = isbn,
            title = volumeInfo.title,
            authors = volumeInfo.authors?.joinToString(", ") ?: "Unknown author",
            description = volumeInfo.description ?: "No description available",
            categories = volumeInfo.categories?.joinToString(", ") ?: "Unknown categories",
            publishedDate = publishedDate,
            pageCount = volumeInfo.pageCount ?: 0,
            thumbnail = volumeInfo.imageLinks?.thumbnail ?: "No thumbnail available"
        )
    }
}
