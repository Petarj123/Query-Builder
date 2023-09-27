package com.petar.querybuilder.insert

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.impl.InsertQueryBuilder
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
class InsertQueryBuilderIntegrationTest(@Autowired val dataSource: DataSource, @Autowired val connectionClient: ConnectionClient
) {

    @Test
    fun `insert should add new row`() {
        // Arrange
        val tableName = "test_users"
        val queryBuilder = InsertQueryBuilder(tableName, connectionClient)
        val jdbcTemplate = JdbcTemplate(dataSource)

        // Act
        queryBuilder
            .select("email", "name")
            .values("'test@example.com'", "'Test User'")
            .execute()


        // Assert
        val result = jdbcTemplate.queryForMap("SELECT * FROM $tableName WHERE email = 'test@example.com'")
        assertEquals("Test User", result["name"])
    }
    @BeforeEach
    fun setUp() {
        val tableName = "test_users"
        val jdbcTemplate = JdbcTemplate(dataSource)
        jdbcTemplate.execute("""
        CREATE TABLE $tableName (
            id SERIAL PRIMARY KEY,
            email VARCHAR(255) NOT NULL,
            name VARCHAR(255) NOT NULL
        )
    """)
    }
    @AfterEach
    fun tearDown() {
        val jdbcTemplate = JdbcTemplate(dataSource)
        jdbcTemplate.update("DROP TABLE test_users;")
    }
}