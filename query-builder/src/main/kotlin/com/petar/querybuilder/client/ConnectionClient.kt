package com.petar.querybuilder.client

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class ConnectionClient(dataSource: DataSource) {

    val jdbcTemplate: JdbcTemplate = JdbcTemplate(dataSource)
}