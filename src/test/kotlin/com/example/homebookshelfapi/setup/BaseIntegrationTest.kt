package com.example.homebookshelfapi.setup

import com.example.homebookshelfapi.domain.entities.Role
import com.example.homebookshelfapi.domain.entities.UserEntity
import com.example.homebookshelfapi.repositories.BookRepository
import com.example.homebookshelfapi.repositories.UserBooksRepository
import com.example.homebookshelfapi.repositories.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.utility.DockerImageName
import org.testcontainers.containers.PostgreSQLContainer
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import generateBookEntity
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.extension.ExtendWith

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("integration")
@TestPropertySource(locations = ["classpath:application-integration.yaml"])
abstract class BaseIntegrationTest {

    @Autowired
    private lateinit var flyway: Flyway

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
        private val postgresContainer: PostgreSQLContainer<*> =
            PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
                .apply {
                    withDatabaseName("testdb")
                    withUsername("postgres")
                    withPassword("password")
                    start()
                }

        @JvmStatic
        @DynamicPropertySource
        fun registerPostgresProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgresContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresContainer.username }
            registry.add("spring.datasource.password") { postgresContainer.password }
            registry.add("spring.flyway.url") { postgresContainer.jdbcUrl }
            registry.add("spring.flyway.user") { postgresContainer.username }
            registry.add("spring.flyway.password") { postgresContainer.password }
            println("Using PostgreSQL: ${postgresContainer.jdbcUrl}")
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            println("PostgreSQL container is running on ${postgresContainer.jdbcUrl}")
        }
    }

    @BeforeEach
    fun setUp() {
        println("Running test setup...")
        insertTestUsers()
        insertTestBooks()
        println("Test setup complete.")
    }

    @AfterEach
    fun tearDown() {
        println("Running test teardown...")
        clearDB()
        println("Test teardown complete.")
    }

    private fun insertTestUsers() {
        val encodedPassword = passwordEncoder.encode("password")
        val testUser = UserEntity(username = "testuser", password = encodedPassword, role = Role.USER)
        val testAdmin = UserEntity(username = "testadmin", password = encodedPassword, role = Role.ADMIN)

        userRepository.saveAll(listOf(testUser, testAdmin))
    }

    private fun insertTestBooks() {
        val book1 = generateBookEntity()
        val book2 = generateBookEntity()
        val book3 = generateBookEntity()
        bookRepository.saveAll(listOf(book1, book2, book3))
    }

    private fun clearDB() {
        userBooksRepository.deleteAll()
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }
}
