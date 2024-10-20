package com.example.homebookshelfapi.domain.entities

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BookTests {

    @Test
    fun `create BookEntity should return valid object`() {
        val book =
            BookEntity(
                isbn = "1234567890",
                title = "Sample Book",
                authors = "Author Name",
                description = "A great book",
                categories = "Fiction",
                publishedDate = LocalDate.of(2023, 5, 1),
                pageCount = 300,
                thumbnail = "sample.jpg"
            )

        assertNotNull(book)
        assertEquals("1234567890", book.isbn)
        assertEquals("Sample Book", book.title)
        assertEquals("Author Name", book.authors)
        assertEquals("A great book", book.description)
        assertEquals("Fiction", book.categories)
        assertEquals(LocalDate.of(2023, 5, 1), book.publishedDate)
        assertEquals(300, book.pageCount)
        assertEquals("sample.jpg", book.thumbnail)
    }

    @Test
    fun `create BookEntity with null optional fields`() {
        val book = BookEntity(isbn = "1234567890", title = "Sample Book", authors = "Author Name")

        assertNotNull(book)
        assertNull(book.description)
        assertNull(book.categories)
        assertNull(book.publishedDate)
        assertNull(book.pageCount)
        assertNull(book.thumbnail)
    }

    @Test
    fun `book entities with different isbn should not be equal`() {
        val book1 = BookEntity(isbn = "1234567890", title = "Book One", authors = "Author One")

        val book2 = BookEntity(isbn = "0987654321", title = "Book One", authors = "Author Two")

        assertNotEquals(book1, book2)
    }
}
