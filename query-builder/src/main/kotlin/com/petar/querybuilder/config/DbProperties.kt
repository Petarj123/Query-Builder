package com.petar.querybuilder.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "db")
data class DbProperties(
    val url: String,
    val username: String,
    val password: String,
    val driverClassName: String
)