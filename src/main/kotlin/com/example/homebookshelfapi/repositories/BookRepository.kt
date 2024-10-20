package com.example.homebookshelfapi.repositories

import com.example.homebookshelfapi.domain.entities.BookEntity
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : JpaRepository<BookEntity, UUID> {
  fun findByIsbn(isbn: String): Optional<BookEntity>
}
