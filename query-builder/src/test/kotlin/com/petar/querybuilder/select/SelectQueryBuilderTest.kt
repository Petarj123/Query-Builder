package com.petar.querybuilder.select

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.impl.SelectQueryBuilder
import com.petar.querybuilder.impl.data.JoinType
import com.petar.querybuilder.impl.data.OrderType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class SelectQueryBuilderTest {
    @Test
    fun `build should construct correct SQL when columns and conditions are specified`() {
        val queryBuilder = createQueryBuilder("test_table")

        queryBuilder.select("id", "name")
            .where("id > 1")
            .where("name LIKE '%test%'")

        // Act
        val query = queryBuilder.build()

        // Assert
        val expectedQuery = "SELECT id, name FROM test_table WHERE id > 1 AND name LIKE '%test%'"
        assertEquals(expectedQuery, query)
    }
    @Test
    fun orderByTest() {
        val queryBuilder = createQueryBuilder("test_table")

        queryBuilder
            .select("*")
            .orderBy("name", OrderType.DESC)
        val query = queryBuilder.build()
        val expectedQuery = "SELECT * FROM test_table ORDER BY name DESC"
        assertEquals(expectedQuery, query)
    }
    @Test
    fun `build should construct correct SQL when multiple order by columns are specified`() {
        val queryBuilder = createQueryBuilder("test_table")

        queryBuilder
            .orderBy("id", OrderType.ASC)
            .orderBy("name", OrderType.DESC)
        val query = queryBuilder.build()

        val expectedQuery = "SELECT * FROM test_table ORDER BY id ASC, name DESC"
        assertEquals(expectedQuery, query)
    }
    @Test
    fun `build should construct correct SQL when both conditions and order by columns are specified`() {
        val queryBuilder = createQueryBuilder("test_table")

        queryBuilder
            .where("id > 5")
            .orderBy("name", OrderType.DESC)
        val query = queryBuilder.build()

        val expectedQuery = "SELECT * FROM test_table WHERE id > 5 ORDER BY name DESC"
        assertEquals(expectedQuery, query)
    }
    @Test
    fun `build should construct correct SQL with complex conditions`() {
        val queryBuilder = createQueryBuilder("test_table")

        queryBuilder
            .where("id > 5")
            .where("age < 30")
            .where("(name LIKE '%test%' OR email LIKE '%test%')")
        val query = queryBuilder.build()

        val expectedQuery = "SELECT * FROM test_table WHERE id > 5 AND age < 30 AND (name LIKE '%test%' OR email LIKE '%test%')"
        assertEquals(expectedQuery, query)
    }
    @Test
    fun `should construct correct SQL with GROUP BY clause`() {
        val queryBuilder = createQueryBuilder("test_table")
        queryBuilder.groupBy("column1", "column2")
        val actualQuery = queryBuilder.build()

        val expectedQuery = "SELECT * FROM test_table GROUP BY column1, column2"
        assertEquals(expectedQuery, actualQuery)
    }

    @Test
    fun `should construct correct SQL with HAVING clause`() {
        val queryBuilder = createQueryBuilder("test_table")

        queryBuilder.having("COUNT(column1) > 1")
        val actualQuery = queryBuilder.build()

        val expectedQuery = "SELECT * FROM test_table HAVING COUNT(column1) > 1"
        assertEquals(expectedQuery, actualQuery)
    }

    @Test
    fun `should construct correct SQL with DISTINCT clause`() {
        val queryBuilder = createQueryBuilder("test_table")

        queryBuilder.distinct()
        val actualQuery = queryBuilder.build()

        val expectedQuery = "SELECT DISTINCT * FROM test_table"
        assertEquals(expectedQuery, actualQuery)
    }

    @Test
    fun `should construct correct SQL with JOIN clause`() {
        val queryBuilder = createQueryBuilder("test_table")

        queryBuilder.join(JoinType.INNER, "another_table", "test_table.id = another_table.id")
        val actualQuery = queryBuilder.build()

        val expectedQuery = "SELECT * FROM test_table INNER JOIN another_table ON test_table.id = another_table.id"
        assertEquals(expectedQuery, actualQuery)
    }

    @Test
    fun `should construct correct SQL with all clauses`() {
        val queryBuilder = createQueryBuilder("test_table")

        queryBuilder
            .select("column1", "column2", "column3")
            .distinct()
            .where("column1 > 1")
            .where("column2 < 10")
            .groupBy("column3")
            .having("COUNT(column3) > 2")
            .orderBy("column2", OrderType.ASC)
            .join(JoinType.INNER, "another_table", "test_table.column1 = another_table.column1")

        val actualQuery = queryBuilder.build()

        val expectedQuery = "SELECT DISTINCT column1, column2, column3 FROM test_table " +
                "WHERE column1 > 1 AND column2 < 10 " +
                "GROUP BY column3 " +
                "HAVING COUNT(column3) > 2 " +
                "ORDER BY column2 ASC " +
                "INNER JOIN another_table ON test_table.column1 = another_table.column1"

        assertEquals(expectedQuery, actualQuery)
    }
    private fun createQueryBuilder(table: String): SelectQueryBuilder {
        val connectionClient: ConnectionClient = mock(ConnectionClient::class.java)
        return SelectQueryBuilder(table, connectionClient)
    }
}