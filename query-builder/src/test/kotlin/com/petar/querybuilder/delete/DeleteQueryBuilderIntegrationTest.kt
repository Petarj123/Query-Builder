package com.petar.querybuilder.delete

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.impl.DeleteQueryBuilder
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
class DeleteQueryBuilderIntegrationTest(@Autowired val dataSource: DataSource, @Autowired val connectionClient: ConnectionClient
) {

    @Test
    fun `should delete row`() {
        val queryBuilder = DeleteQueryBuilder("test_users", connectionClient)

        val execute = queryBuilder.where("email = 'email1'").execute()

        assertEquals(1, execute)
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
        jdbcTemplate.execute("INSERT INTO $tableName(email, name) VALUES('email1', 'petar')")
        jdbcTemplate.execute("INSERT INTO $tableName(email, name) VALUES('email2', 'petar2')")

    }
    @AfterEach
    fun tearDown() {
        val jdbcTemplate = JdbcTemplate(dataSource)
        jdbcTemplate.update("DROP TABLE test_users;")
    }
}