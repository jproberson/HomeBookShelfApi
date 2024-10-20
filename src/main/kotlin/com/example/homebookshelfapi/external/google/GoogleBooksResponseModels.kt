package com.example.homebookshelfapi.external.google

data class GoogleBooksResponse(val items: List<GoogleBookItem>?)

data class GoogleBookItem(val volumeInfo: VolumeInfo)

data class VolumeInfo(
  val title: String,
  val authors: List<String>?,
  val publishedDate: String?,
  val pageCount: Int?,
  val categories: List<String>?,
  val description: String?,
  val imageLinks: ImageLinks?
)

data class ImageLinks(val thumbnail: String?)
