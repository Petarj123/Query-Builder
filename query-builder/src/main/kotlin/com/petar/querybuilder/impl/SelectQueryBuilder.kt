package com.petar.querybuilder.impl

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.core.BaseQueryBuilder
import org.springframework.jdbc.core.JdbcTemplate

class SelectQueryBuilder(private val table: String, connectionClient: ConnectionClient) : BaseQueryBuilder<SelectQueryBuilder>() {

    override val jdbcTemplate: JdbcTemplate = connectionClient.jdbcTemplate
    override val queryType: QueryType = QueryType.SELECT

    override fun build(): String {
        val selectedColumns = if (columns.isEmpty()) "*" else columns.joinToString(", ")
        val whereCause = if (conditions.isEmpty()) "" else "WHERE ${conditions.joinToString(" AND ")}"

        return "SELECT $selectedColumns FROM $table $whereCause".trim()
    }



}