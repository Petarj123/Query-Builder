package com.petar.querybuilder.update

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.impl.UpdateQueryBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import javax.sql.DataSource

@SpringBootTest
@ActiveProfiles("test")
class UpdateQueryBuilderIntegrationTest(
    @Autowired val dataSource: DataSource,
    @Autowired val connectionClient: ConnectionClient
) {
    val tableName = "test_users"
    val jdbcTemplate = JdbcTemplate(dataSource)

    @BeforeEach
    fun setUp() {
        jdbcTemplate.execute("""
            CREATE TABLE $tableName (
                id SERIAL PRIMARY KEY,
                email VARCHAR(255) NOT NULL,
                name VARCHAR(255) NOT NULL,
                age INT
            )
        """)

        jdbcTemplate.update("INSERT INTO $tableName(email, name, age) VALUES('test@example.com', 'Test User', 10);")
    }
    @AfterEach
    fun tearDown() {
        jdbcTemplate.execute("DROP TABLE $tableName")
    }
    @Test
    fun `update should modify existing row`() {
        val queryBuilder = createQueryBuilder()

        queryBuilder
            .where("email", "=", "test@example.com")
            .set("name", "Updated User")
            .execute()

        val result = jdbcTemplate.queryForMap("SELECT * FROM $tableName WHERE email = 'test@example.com'")
        assertEquals("Updated User", result["name"])

    }
    @Test
    fun `update should modify age`() {
        val queryBuilder = createQueryBuilder()

        queryBuilder
            .where("email", "=", "test@example.com")
            .set("age", 12)
            .execute()

        val result = jdbcTemplate.queryForMap("SELECT * FROM $tableName WHERE email = 'test@example.com'")
        assertEquals(12, result["age"])
    }

    private fun createQueryBuilder(): UpdateQueryBuilder {
        return UpdateQueryBuilder(tableName, connectionClient)
    }
}
