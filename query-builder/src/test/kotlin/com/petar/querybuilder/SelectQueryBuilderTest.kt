package com.petar.querybuilder

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.impl.SelectQueryBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class SelectQueryBuilderTest {
    @Test
    fun `build should construct correct SQL when columns and conditions are specified`() {
        // Arrange
        val tableName = "test_table"
        val connectionClient: ConnectionClient = mock()
        val queryBuilder = SelectQueryBuilder(tableName, connectionClient)

        queryBuilder.select("id", "name")
            .where("id > 1")
            .where("name LIKE '%test%'")

        // Act
        val query = queryBuilder.build()

        // Assert
        val expectedQuery = "SELECT id, name FROM test_table WHERE id > 1 AND name LIKE '%test%'"
        assertEquals(expectedQuery, query)
    }
}