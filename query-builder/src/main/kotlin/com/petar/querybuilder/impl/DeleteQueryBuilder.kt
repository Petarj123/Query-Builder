package com.petar.querybuilder.impl

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.core.BaseQueryBuilder
import com.petar.querybuilder.impl.data.Condition
import com.petar.querybuilder.impl.data.QueryType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter

class DeleteQueryBuilder(private val table: String, private val connectionClient: ConnectionClient) : BaseQueryBuilder<DeleteQueryBuilder>() {

    override val jdbcTemplate: JdbcTemplate = connectionClient.jdbcTemplate
    override val queryType: QueryType = QueryType.DELETE

    override fun build(): String {
        val whereClause = conditions.joinToString(" AND ") { "${it.column} = ?" }

        if (whereClause.isBlank()) {
            throw IllegalArgumentException("Where clause is mandatory for DELETE query to avoid deleting all records")
        }

        return "DELETE FROM $table WHERE $whereClause"
    }
    fun where(column: String, comparator:String, value: Any): DeleteQueryBuilder {
        conditions.add(Condition(column, comparator, value))
        return this
    }

    override fun execute(): Any {
        val query = build()

        val preparedStatementSetter = PreparedStatementSetter { ps ->
            conditions.forEachIndexed { index, condition ->
                ps.setObject(index + 1, condition.value)
            }
        }

        return jdbcTemplate.update(query, preparedStatementSetter)
    }
}