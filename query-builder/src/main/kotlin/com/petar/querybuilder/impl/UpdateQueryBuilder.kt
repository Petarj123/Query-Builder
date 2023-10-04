package com.petar.querybuilder.impl

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.core.BaseQueryBuilder
import com.petar.querybuilder.impl.data.Condition
import com.petar.querybuilder.impl.data.QueryType
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
        val whereClause = conditions.joinToString(" AND ") { "${it.column} ${it.comparator} ?" }

        return "UPDATE $table SET $setClause WHERE $whereClause"
    }

    fun where(column: String, comparator: String, value: Any): UpdateQueryBuilder {
        conditions.add(Condition(column, comparator, value))
        return this
    }
    override fun execute(): Any {
        val sql = build()

        val allParams = updateValues.values + conditions.map { it.value }
        val preparedStatementSetter = PreparedStatementSetter { ps ->
            var paramIndex = 1
            allParams.forEach { ps.setObject(paramIndex++, it) }
        }

        return jdbcTemplate.update(sql, preparedStatementSetter)
    }

}