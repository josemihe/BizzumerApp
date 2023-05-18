package com.je.bizzumer.io.response

import com.je.bizzumer.model.User

data class LoginResponse (
    val user : User,
    val token: String
    )
