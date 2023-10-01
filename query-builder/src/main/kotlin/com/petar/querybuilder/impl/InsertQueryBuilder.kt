package com.petar.querybuilder.impl

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.core.BaseQueryBuilder
import com.petar.querybuilder.impl.data.QueryType
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class InsertQueryBuilder(private val table: String, private val connectionClient: ConnectionClient) : BaseQueryBuilder<InsertQueryBuilder>() {

    override val jdbcTemplate: JdbcTemplate = connectionClient.jdbcTemplate
    override val queryType: QueryType = QueryType.INSERT
    private val values = mutableListOf<Any>()

    override fun build(): String {
        val selectedColumns = columns.joinToString(", ", prefix = "(", postfix = ")")
        val placeholders = columns.joinToString(", ") { "?" } // Creates placeholders for each column
        return "INSERT INTO $table $selectedColumns VALUES ($placeholders)"
    }

    override fun execute(): Any {
        val sql = build()
        println(sql)
        return jdbcTemplate.update(sql, *values.toTypedArray())
    }

    fun into(vararg cols: String): InsertQueryBuilder {
        columns.addAll(cols)
        return this
    }
    fun where(condition: String): InsertQueryBuilder {
        conditions.add(condition)
        return this
    }
    fun values(vararg args: Any): BaseQueryBuilder<InsertQueryBuilder> {
        values.addAll(args)
        return this
    }

}