package com.example.homebookshelfapi

import com.example.homebookshelfapi.setup.BaseIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
class ApplicationTests : BaseIntegrationTest() {

    @Test
    fun contextLoads() {
    }
}
