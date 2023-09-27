package com.petar.querybuilder.impl

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.core.BaseQueryBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter

class UpdateQueryBuilder(private val table: String, private val connectionClient: ConnectionClient) : BaseQueryBuilder<UpdateQueryBuilder>() {
    override val jdbcTemplate: JdbcTemplate = connectionClient.jdbcTemplate
    override val queryType: QueryType = QueryType.UPDATE
    private val updateValues = mutableMapOf<String, Any>()

    fun set(column: String, value: Any): UpdateQueryBuilder {
        updateValues[column] = value
        return this
    }

    override fun build(): String {
        if (updateValues.isEmpty()) throw IllegalArgumentException("No update values provided")
        if (conditions.isEmpty()) throw IllegalArgumentException("No conditions provided for update")

        val setClause = updateValues.keys.joinToString(", ") { "$it = ?" }
        val whereClause = conditions.joinToString(" AND ") // Assuming conditions are also parameterized

        return "UPDATE $table SET $setClause WHERE $whereClause"
    }

    override fun execute(): Any? {
        val query = build()

        val preparedStatementSetter = PreparedStatementSetter { ps ->
            var paramIndex = 1
            updateValues.values.forEach { ps.setObject(paramIndex++, it) }
            // If you need to set values for conditions, you should do that here.
        }

        return jdbcTemplate.update(query, preparedStatementSetter) // Execute the update and return the number of rows updated
    }
}