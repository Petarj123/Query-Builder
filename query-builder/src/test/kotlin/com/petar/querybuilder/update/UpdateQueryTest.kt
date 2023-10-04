package com.petar.querybuilder.update

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.impl.UpdateQueryBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import javax.sql.DataSource

@SpringBootTest
@ActiveProfiles("test")
class UpdateQueryTest(@Autowired val dataSource: DataSource, @Autowired val connectionClient: ConnectionClient
) {

    @Test
    fun testUpdate() {
        val queryBuilder = UpdateQueryBuilder("test", connectionClient)
        val query = queryBuilder
            .where("name", "=", "petar")
            .set("email", "updated")
            .build()

        val expectedQuery = "UPDATE test SET email = ? WHERE name = ?"
        assertEquals(expectedQuery, query)
    }
}