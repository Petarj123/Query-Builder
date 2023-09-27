package com.petar.querybuilder.core

import org.springframework.jdbc.core.JdbcTemplate

abstract class BaseQueryBuilder : QueryBuilder {
    protected val conditions = mutableListOf<String>()
    protected val columns = mutableListOf<String>()

    abstract val jdbcTemplate: JdbcTemplate

    /**
     * Adds a condition to the query.
     * @param condition The condition to be added to the query.
     */
    fun where(condition: String): BaseQueryBuilder {
        conditions.add(condition)
        return this
    }

    /**
     * Specifies the columns to be selected in the query.
     * @param cols The columns to be selected.
     */
    fun select(vararg cols: String): BaseQueryBuilder {
        columns.addAll(cols)
        return this
    }

    override fun execute(): List<Map<String, Any>> {
        val query = build()
        return jdbcTemplate.queryForList(query)
    }

}