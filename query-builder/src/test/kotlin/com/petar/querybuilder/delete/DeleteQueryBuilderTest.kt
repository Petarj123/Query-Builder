package com.petar.querybuilder.delete

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.impl.DeleteQueryBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class DeleteQueryBuilderTest {

    @Test
    fun testDelete() {
        val queryBuilder = createQueryBuilder("test_table")
        val query = queryBuilder.where("name = 'petar'").build()

        val actualQuery = "DELETE FROM test_table WHERE name = 'petar'"

        assertEquals(actualQuery, query)
    }


    private fun createQueryBuilder(table: String): DeleteQueryBuilder {
        val connectionClient: ConnectionClient = Mockito.mock(ConnectionClient::class.java)
        return DeleteQueryBuilder(table, connectionClient)
    }
}