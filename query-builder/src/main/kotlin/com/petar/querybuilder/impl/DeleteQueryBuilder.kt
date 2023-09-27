package com.petar.querybuilder.impl

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.core.BaseQueryBuilder
import org.springframework.jdbc.core.JdbcTemplate

class DeleteQueryBuilder(private val table: String, private val connectionClient: ConnectionClient) : BaseQueryBuilder<DeleteQueryBuilder>() {

    override val jdbcTemplate: JdbcTemplate = connectionClient.jdbcTemplate
    override val queryType: QueryType = QueryType.DELETE

    override fun build(): String {
        val whereClause = conditions.joinToString(" AND ")

        if (whereClause.isBlank()) throw IllegalArgumentException("Where clause is mandatory for DELETE query to avoid deleting all records")

        return "DELETE FROM $table WHERE $whereClause"
    }
}