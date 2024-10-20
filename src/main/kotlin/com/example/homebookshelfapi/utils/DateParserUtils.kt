package com.example.homebookshelfapi.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateParserUtil {
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  fun parseDate(dateStr: String?): LocalDate? {
    return if (!dateStr.isNullOrBlank()) {
      runCatching { LocalDate.parse(dateStr, formatter) }.getOrNull()
    } else {
      null
    }
  }
}
