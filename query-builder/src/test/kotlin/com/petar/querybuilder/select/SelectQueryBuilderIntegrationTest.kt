package com.petar.querybuilder.select

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.impl.SelectQueryBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

        jdbcTemplate.execute("CREATE TABLE test_table (id SERIAL PRIMARY KEY, name VARCHAR(255))")
        jdbcTemplate.update("INSERT INTO test_table (name) VALUES ('John Doe')")

        // Act
        queryBuilder.select("id", "name")
        val result: Any? = queryBuilder.execute()

        // Assert
        assertTrue(result is List<*>, "Result should be a List")
        val resultList = result as List<Map<String, Any>>
        assertEquals(1, resultList.size)
        assertEquals(1, resultList[0]["id"])
        assertEquals("John Doe", resultList[0]["name"])

        jdbcTemplate.execute("DROP TABLE test_table")
    }
}