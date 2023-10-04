package com.petar.querybuilder.impl

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.core.BaseQueryBuilder
import com.petar.querybuilder.impl.data.*
import org.springframework.jdbc.core.JdbcTemplate

class SelectQueryBuilder(private val table: String, connectionClient: ConnectionClient) : BaseQueryBuilder<SelectQueryBuilder>() {

    override val jdbcTemplate: JdbcTemplate = connectionClient.jdbcTemplate
    override val queryType: QueryType = QueryType.SELECT
    override fun execute(): Any {
        val sql = build()

        val params = conditions.map { it.value }.toTypedArray()
        return jdbcTemplate.queryForList(sql, *params)
    }


    private val orderByColumns = mutableListOf<OrderByColumn>()
    private val groupByColumns = mutableListOf<String>()
    private var havingCondition: String? = null
    private var isDistinct:Boolean = false
    private val joins = mutableListOf<Join>()


    override fun build(): String {
        val queryBuilder = StringBuilder()

        // Start with "SELECT", and if "isDistinct" is true, append "DISTINCT"
        queryBuilder.append("SELECT ")
        if (isDistinct) queryBuilder.append("DISTINCT ")

        // Then append the columns or '*' if no columns are specified
        if (columns.isEmpty()) {
            queryBuilder.append("*")
        } else {
            queryBuilder.append(columns.joinToString(", "))
        }

        // From clause is mandatory, append it next
        queryBuilder.append(" FROM $table")

        // If there are conditions specified, construct the WHERE clause
        if (conditions.isNotEmpty()) {
            val conditionStrings = conditions.map { "${it.column} ${it.comparator} ?" }
            queryBuilder.append(" WHERE ").append(conditionStrings.joinToString(" AND "))
        }

        // If there are GROUP BY columns specified, construct the GROUP BY clause
        if (groupByColumns.isNotEmpty()) {
            queryBuilder.append(" GROUP BY ")
                .append(groupByColumns.joinToString(", "))
        }

        // If there is a having condition, construct the HAVING clause
        if (havingCondition?.isNotEmpty() == true) {
            queryBuilder.append(" HAVING ").append(havingCondition)
        }

        // If there are ORDER BY columns specified, construct the ORDER BY clause
        if (orderByColumns.isNotEmpty()) {
            val orderByClause = orderByColumns.joinToString(", ") { "${it.columnName} ${it.orderType}" }
            queryBuilder.append(" ORDER BY $orderByClause")
        }
        joins.forEach {
            queryBuilder.append(" ${it.type} JOIN ${it.table} ON ${it.condition}")
        }
        return queryBuilder.toString()
    }
    fun select(vararg cols: String): SelectQueryBuilder {
        columns.addAll(cols)
        return this
    }
    fun where(column: String, comparator: String, value: Any): SelectQueryBuilder {
        conditions.add(Condition(column, comparator, value))
        return this
    }
    fun orderBy(columnName: String, orderType: OrderType): SelectQueryBuilder {
        orderByColumns.add(OrderByColumn(columnName, orderType))
        return this
    }

    fun groupBy(vararg columnName: String): SelectQueryBuilder {
        groupByColumns.addAll(columnName)
        return this
    }

    fun having(condition: String): SelectQueryBuilder {
        havingCondition = condition
        return this
    }
    fun distinct(): SelectQueryBuilder {
        isDistinct = true
        return this
    }
    fun join(type: JoinType, table: String, condition: String): SelectQueryBuilder {
        joins.add(Join(type, table, condition))
        return this
    }
}