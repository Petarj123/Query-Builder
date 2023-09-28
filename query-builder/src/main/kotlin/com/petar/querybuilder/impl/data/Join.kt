package com.petar.querybuilder.impl.data

data class Join(val type:JoinType, val table: String, val condition: String)
