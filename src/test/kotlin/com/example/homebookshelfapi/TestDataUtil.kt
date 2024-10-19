import com.example.homebookshelfapi.domain.entities.*
import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.books.BooksFaker
import io.github.serpro69.kfaker.commerce.CommerceFaker
import java.time.LocalDate
import java.util.*

val booksFaker = BooksFaker()
val faker = Faker()
val commerceFaker = CommerceFaker()

val DEFAULT_TEST_USER_ID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")

fun testUserEntity(
  id: UUID? = null,
  username: String = faker.name.name(),
  password: String = faker.crypto.md5(),
  role: Role = Role.USER,
  createdAt: Date = Date(),
  updatedAt: Date = Date()
) =
  UserEntity(
    id = id ?: DEFAULT_TEST_USER_ID,
    username = username,
    password = password,
    role = role,
    updatedAt = updatedAt,
    createdAt = createdAt
  )

fun testBookEntity(
  id: UUID? = null,
  isbn: String = commerceFaker.barcode.isbn(),
  title: String = booksFaker.book.title(),
  authors: String = booksFaker.book.author(),
  description: String = faker.random.randomString(),
  categories: String = booksFaker.book.genre(),
  publishedDate: LocalDate? = null,
  pageCount: Int = faker.random.nextInt(100, 500),
  thumbnail: String = faker.internet.domain()
) =
  BookEntity(
    id = id ?: UUID.randomUUID(),
    isbn = isbn,
    title = title,
    authors = authors,
    description = description,
    categories = categories,
    publishedDate = publishedDate,
    pageCount = pageCount,
    thumbnail = thumbnail
  )

fun testUserBooksEntity(
  id: UUID? = null,
  user: UserEntity = testUserEntity(),
  book: BookEntity = testBookEntity(),
  addedAt: Date = Date()
) = UserBooksEntity(id = id ?: UUID.randomUUID(), user = user, book = book, addedAt = addedAt)

fun testRecommendedBooksEntity(
  id: UUID? = null,
  user: UserEntity = testUserEntity(),
  book: BookEntity = testBookEntity(),
  recommendedAt: Date = Date(),
  recommendationStrategy: String = "Default"
) =
  RecommendedBooksEntity(
    id = id ?: UUID.randomUUID(),
    user = user,
    book = book,
    recommendedAt = recommendedAt,
    recommendationStrategy = recommendationStrategy
  )
