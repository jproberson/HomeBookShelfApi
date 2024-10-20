package com.example.homebookshelfapi.external.google

import com.example.homebookshelfapi.domain.entities.BookEntity

interface GoogleApiService {
  fun fetchBookInfoByISBN(isbn: String): BookEntity?
}
