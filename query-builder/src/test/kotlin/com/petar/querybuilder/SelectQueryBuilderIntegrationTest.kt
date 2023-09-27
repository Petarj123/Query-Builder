package com.petar.querybuilder

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.impl.SelectQueryBuilder
import org.junit.jupiter.api.Assertions.assertEquals
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
    @Test
    fun `build should interact correctly with real database`() {
        // Arrange
        val tableName = "test_table"
        val queryBuilder = SelectQueryBuilder(tableName, connectionClient)

        // Initialize the test database with some data.
        val jdbcTemplate = JdbcTemplate(dataSource)

        jdbcTemplate.execute("CREATE TABLE test_table (id INT, name VARCHAR(255))")
        jdbcTemplate.update("INSERT INTO test_table (id, name) VALUES (1, 'John Doe')")

        // Act
        queryBuilder.select("id", "name")
        val result = queryBuilder.execute()

        // Assert
        assertEquals(1, result.size)
        assertEquals(1, result[0]["id"])
        assertEquals("John Doe", result[0]["name"])

        jdbcTemplate.execute("DROP TABLE test_table")
    }
}