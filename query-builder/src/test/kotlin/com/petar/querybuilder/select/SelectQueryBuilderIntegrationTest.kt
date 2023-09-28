package com.petar.querybuilder.select

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.impl.SelectQueryBuilder
import com.petar.querybuilder.impl.data.JoinType
import com.petar.querybuilder.impl.data.OrderType
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import javax.sql.DataSource

@SpringBootTest
@ActiveProfiles("test")
class SelectQueryBuilderIntegrationTest(@Autowired val dataSource: DataSource, @Autowired val connectionClient: ConnectionClient
) {
    val tableName = "test_users"
    val joinedTable = "test_posts"
    @BeforeEach
    fun setUp() {
        val table1 = "test_users"
        val table2 = "test_posts"

        val jdbcTemplate = JdbcTemplate(dataSource)

        jdbcTemplate.execute(
            """
            CREATE TABLE $table1 (
                user_id SERIAL PRIMARY KEY, 
                name VARCHAR(30) NOT NULL, 
                email VARCHAR(30)
            )
        """
        )

        jdbcTemplate.execute(
            """
            CREATE TABLE $table2 (
                post_id SERIAL PRIMARY KEY, 
                user_id INTEGER,
                title VARCHAR(30), 
                content VARCHAR(30),
                CONSTRAINT fk_$table1 FOREIGN KEY (user_id) REFERENCES $table1(user_id)
            )
        """
        )

        // Insert 5 users
        for (i in 1..5) {
            jdbcTemplate.update("INSERT INTO $table1(name, email) VALUES (?, ?)", "User$i", "user$i@example.com")
        }

        // Insert 10 posts
        for (i in 1..10) {
            val userId = (i % 5) + 1  // To distribute posts among users
            jdbcTemplate.update(
                "INSERT INTO $table2(user_id, title, content) VALUES (?, ?, ?)",
                userId,
                "Title$i",
                "Content$i"
            )
        }
    }
    @AfterEach
    fun tearDown() {
        val table1 = "test_users"
        val table2 = "test_posts"

        val jdbcTemplate = JdbcTemplate(dataSource)

        jdbcTemplate.execute("DROP TABLE $table2")
        jdbcTemplate.execute("DROP TABLE $table1")
    }

    @Test
    fun `build should interact correctly with real database`() {
        // Arrange
        val tableName = "test_users"
        val queryBuilder = SelectQueryBuilder(tableName, connectionClient)

        // Initialize the test database with some data.
        val jdbcTemplate = JdbcTemplate(dataSource)

        // Act
        queryBuilder.select("user_id", "name")
        val result: Any? = queryBuilder.execute()

        // Assert
        assertTrue(result is List<*>, "Result should be a List")
        val resultList = result as List<Map<String, Any>>
        assertEquals(5, resultList.size)
        assertEquals("User1", resultList[0]["name"])
    }
    @Test
    fun `should construct SQL queries with orderBy correctly and interact with real database`() {
        val queryBuilder = SelectQueryBuilder(tableName, connectionClient)
        queryBuilder.select("user_id", "name", "email").orderBy("name", OrderType.ASC)
        val orderedList: List<Map<String, Any>> = queryBuilder.execute() as List<Map<String, Any>>

        assertEquals(5, orderedList.size)
        assertEquals("User1", orderedList[0]["name"])
    }

    @Test
    fun `should construct SQL queries with groupBy and having correctly and interact with real database`() {
        val queryBuilder = SelectQueryBuilder(tableName, connectionClient)
        queryBuilder.groupBy("user_id").having("COUNT(user_id) > 1")
        val groupedList: List<Map<String, Any>> = queryBuilder.execute() as List<Map<String, Any>>

        assertTrue(groupedList.isEmpty()) // Assuming no user_id repeats more than once in test_users
    }

    @Test
    fun `should construct SQL queries with distinct correctly and interact with real database`() {
        val queryBuilder = SelectQueryBuilder(tableName, connectionClient)
        queryBuilder.distinct().select("name")
        val distinctList: List<Map<String, Any>> = queryBuilder.execute() as List<Map<String, Any>>

        assertEquals(5, distinctList.size) // Assuming all names are unique in test_users
    }

    @Test
    fun `should construct SQL queries with join correctly and interact with real database`() {
        val queryBuilder = SelectQueryBuilder(tableName, connectionClient)
        queryBuilder.join(JoinType.INNER, joinedTable, "$tableName.user_id = $joinedTable.user_id")
        val joinedList: List<Map<String, Any>> = queryBuilder.execute() as List<Map<String, Any>>

        assertEquals(10, joinedList.size) // Assuming each user has two posts
    }

}