package com.petar.querybuilder.insert

import java.util.Date

data class TestUser(val id:Int, val name:String, val email:String, val createdAt: Date) {
    constructor(id: Int, name: String, email: String) : this(id, name, email, Date())
}

