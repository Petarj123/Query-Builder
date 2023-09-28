package com.petar.querybuilder.core

import com.petar.querybuilder.impl.data.QueryType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import java.sql.Statement

abstract class BaseQueryBuilder<T : BaseQueryBuilder<T>> : QueryBuilder {

    protected val conditions = mutableListOf<String>()
    protected val columns = mutableListOf<String>()

    abstract val jdbcTemplate: JdbcTemplate
    abstract val queryType: QueryType


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