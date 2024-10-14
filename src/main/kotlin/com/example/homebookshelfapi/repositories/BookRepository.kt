package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BookRepository : JpaRepository<Book, UUID> {
    fun findByIsbn(isbn: String): Optional<Book>

}
