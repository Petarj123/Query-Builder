package com.petar.querybuilder.core

import com.petar.querybuilder.impl.data.Condition
import com.petar.querybuilder.impl.data.QueryType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import java.sql.Statement

abstract class BaseQueryBuilder<T : BaseQueryBuilder<T>> : QueryBuilder {

    val conditions = mutableListOf<Condition>()
    protected val columns = mutableListOf<String>()

    abstract val jdbcTemplate: JdbcTemplate
    abstract val queryType: QueryType

    abstract override fun execute(): Any;
}