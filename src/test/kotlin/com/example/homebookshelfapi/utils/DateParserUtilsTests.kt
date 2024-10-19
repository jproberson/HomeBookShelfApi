package com.example.homebookshelfapi.utils

import org.junit.jupiter.api.Test

class DateParserUtilsTests {

  @Test
  fun parseDate_ShouldReturnDateWhenDateStrIsValid() {
    val date = DateParserUtil.parseDate("2021-01-01")
    assert(date != null)
  }

  @Test
  fun parseDate_ShouldReturnNullWhenDateStrIsNull() {
    val date = DateParserUtil.parseDate(null)
    assert(date == null)
  }

  @Test
  fun parseDate_ShouldReturnNullWhenDateStrIsInvalid() {
    val date = DateParserUtil.parseDate("2021-13-01")
    assert(date == null)
  }
}
