package com.petar.querybuilder

import com.petar.querybuilder.impl.SelectQueryBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QueryBuilderApplication

fun main(args: Array<String>) {
	runApplication<QueryBuilderApplication>(*args)
}
