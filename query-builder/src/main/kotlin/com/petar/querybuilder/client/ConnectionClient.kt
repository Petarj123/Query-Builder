package com.petar.querybuilder.client

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

@Component
class ConnectionClient(private val dataSource: DataSource) {

    val jdbcTemplate: JdbcTemplate = JdbcTemplate(dataSource)
    fun getConnection(): Connection = dataSource.connection ?: throw SQLException("Unable to get Connection")

}