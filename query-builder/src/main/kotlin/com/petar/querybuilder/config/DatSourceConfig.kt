package com.petar.querybuilder.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties(DbProperties::class)
class DatSourceConfig(private val dbProperties: DbProperties) {

    @Bean
    fun dataSource(): DataSource {
        val dataSourceBuilder = DataSourceBuilder.create()
            .driverClassName(dbProperties.driverClassName)
            .url(dbProperties.url)
            .username(dbProperties.username)
            .password(dbProperties.password)
        return dataSourceBuilder.build()
    }
}