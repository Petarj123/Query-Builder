package com.petar.querybuilder.insert

import com.fasterxml.jackson.databind.ObjectMapper
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
import java.util.Date
import javax.sql.DataSource

@SpringBootTest
@ActiveProfiles("test")
class InsertQueryBuilderIntegrationTest(@Autowired val dataSource: DataSource, @Autowired val connectionClient: ConnectionClient
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
            created_at TIMESTAMP
        )
    """)
    }
    @AfterEach
    fun tearDown() {
        val jdbcTemplate = JdbcTemplate(dataSource)
        jdbcTemplate.update("DROP TABLE test_users;")
    }
    @Test
    fun `should insert user with without id specified`() {
        val testUser = TestUser(1, "Petar", "petar@gmail.com")
        val insert = createQueryBuilder()

        insert
            .into("name", "email")
            .values(testUser.name, testUser.email)
            .execute();
        val result = jdbcTemplate.queryForMap("SELECT * FROM $tableName")
        assertEquals(result["id"], 1)
        assertEquals(result["name"], testUser.name)
        assertEquals(result["email"], testUser.email)
    }
    @Test
    fun `should insert user with id specified`() {
        val testUser = TestUser(1, "Petar", "petar@gmail.com")
        val insert = createQueryBuilder()

        insert
            .into("id", "name", "email")
            .values(3, testUser.name, testUser.email)
            .execute();
        val result = jdbcTemplate.queryForMap("SELECT * FROM $tableName")
        assertEquals(result["id"], 3)
        assertEquals(result["name"], testUser.name)
        assertEquals(result["email"], testUser.email)
    }
    @Test
    fun `should insert user and return json`() {
        val testUser = TestUser(1, "Petar", "petar@gmail.com")
        val insert = createQueryBuilder()

        insert
            .into("name", "email")
            .values(testUser.name, testUser.email)
            .execute();
        val result = jdbcTemplate.queryForMap("SELECT * FROM $tableName")
        val objectMapper = ObjectMapper()
        val actualJson = objectMapper.writeValueAsString(result)

        val expectedJson = """{"id":1,"email":"petar@gmail.com","name":"Petar","created_at":null}"""
        assertEquals(expectedJson, actualJson)
    }
    @Test
    fun `should insert user with created_at specified`() {
        val testUser = TestUser(1, "Petar", "petar@gmail.com", Date())
        val insert = createQueryBuilder()

        insert
            .into("name", "email", "created_at")
            .values(testUser.name, testUser.email, testUser.createdAt)
            .execute()
        val result = jdbcTemplate.queryForMap("SELECT * FROM $tableName")
        val retrievedDate = result["created_at"] as Date

        assertEquals(testUser.createdAt.time, retrievedDate.time)
    }

    private fun createQueryBuilder(): InsertQueryBuilder {
        return InsertQueryBuilder(tableName, connectionClient)
    }
}