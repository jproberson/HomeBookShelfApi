package com.example.homebookshelfapi.controllers

import com.example.homebookshelfapi.domain.entities.Role
import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.repositories.BookRepository
import com.example.homebookshelfapi.repositories.UserBooksRepository
import com.example.homebookshelfapi.repositories.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.TestPropertySourceUtils
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import generateBookEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.testcontainers.containers.wait.strategy.Wait

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension::class)
@Testcontainers
@ContextConfiguration(initializers = [BaseIntegrationTest.Initializer::class])
abstract class BaseIntegrationTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var userBooksRepository: UserBooksRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    companion object {
        @Container
        val postgresContainer =
            PostgreSQLContainer<Nothing>("postgres:latest").apply {
                withDatabaseName("testdb")
                withUsername("postgres")
                withPassword("password")
                waitingFor(Wait.forListeningPort())
                waitingFor(
                    Wait.forLogMessage(
                        ".*database system is ready to accept connections.*\\n",
                        1
                    )
                )
            }

        @JvmStatic
        @BeforeAll
        fun startContainer() {
            println("Connect to: jdbc:postgresql://localhost:${postgresContainer.firstMappedPort}/testdb")
            println("Username: ${postgresContainer.username}")
            println("Password: ${postgresContainer.password}")

            postgresContainer.start()
        }
    }

    @BeforeEach
    fun setUp() {
        insertTestUser()
        insertTestBooks()
    }

    @AfterEach
    fun tearDown() {
        clearUsers()
    }

    private fun insertTestUser() {
        val encodedPassword = passwordEncoder.encode("password")
        val testUser =
            UserEntity(
                username = "testuser",
                password = encodedPassword,
                role = Role.USER
            )
        userRepository.save(testUser)
    }

    private fun insertTestBooks() {
        val book1 = generateBookEntity()
        val book2 = generateBookEntity()
        val book3 = generateBookEntity()
        bookRepository.saveAll(listOf(book1, book2, book3))
    }

    private fun clearUsers() {
        userBooksRepository.deleteAll()
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }

    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.datasource.url=${postgresContainer.jdbcUrl}",
                "spring.datasource.username=${postgresContainer.username}",
                "spring.datasource.password=${postgresContainer.password}"
            )
        }
    }
}
