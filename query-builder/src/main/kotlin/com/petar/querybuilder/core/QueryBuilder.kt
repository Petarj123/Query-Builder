package com.petar.querybuilder.core

interface QueryBuilder {

    fun build(): String
    fun execute(): Any
}