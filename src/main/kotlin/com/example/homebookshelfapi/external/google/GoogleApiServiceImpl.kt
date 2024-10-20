package com.example.homebookshelfapi.external.google

import com.example.homebookshelfapi.domain.entities.BookEntity
import com.example.homebookshelfapi.external.ApiEndpoints
import com.example.homebookshelfapi.utils.DateParserUtil
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.util.*

@Profile("prod")
@Service
class GoogleApiServiceImpl(private val restTemplate: RestTemplate) : GoogleApiService {

    override fun fetchBookInfoByISBN(isbn: String): BookEntity? {
        val url = "${ApiEndpoints.GOOGLE_BOOKS_API}$isbn"
        val response = restTemplate.getForObject<GoogleBooksResponse>(url)

        val volumeInfo = response.items?.firstOrNull()?.volumeInfo ?: return null
        val publishedDate = DateParserUtil.parseDate(volumeInfo.publishedDate)

        return BookEntity(
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
