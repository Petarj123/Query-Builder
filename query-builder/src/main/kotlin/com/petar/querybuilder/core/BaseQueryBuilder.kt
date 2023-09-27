package com.petar.querybuilder.core

import com.petar.querybuilder.impl.QueryType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import java.sql.Statement

abstract class BaseQueryBuilder<T : BaseQueryBuilder<T>> : QueryBuilder {

    protected val conditions = mutableListOf<String>()
    protected val columns = mutableListOf<String>()

    abstract val jdbcTemplate: JdbcTemplate
    abstract val queryType: QueryType

    /**
     * Adds a condition to the query.
     * @param condition The condition to be added to the query.
     */
    fun where(condition: String): T {
        conditions.add(condition)
        @Suppress("UNCHECKED_CAST")
        return this as T
    }

    /**
     * Specifies the columns to be selected in the query.
     * @param cols The columns to be selected.
     */
    fun select(vararg cols: String): T {
        columns.addAll(cols)
        @Suppress("UNCHECKED_CAST")
        return this as T
    }
    override fun execute(): Any? {
        val query = build()
        return when(queryType) {
            QueryType.SELECT -> jdbcTemplate.queryForList(query)
            QueryType.INSERT -> {
                val keyHolder = GeneratedKeyHolder()
                jdbcTemplate.update({ con ->
                    con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
                }, keyHolder)
                keyHolder.keys // Returning the generated keys
            }
            else -> jdbcTemplate.update(query)  // For UPDATE, DELETE
        }
    }


}