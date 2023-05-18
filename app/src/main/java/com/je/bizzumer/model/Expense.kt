package com.je.bizzumer.model

import java.util.Date

data class Expense(
    val id: Int,
    val user_id: Int,
    val group_id: Int,
    val amount: Double,
    val created_at: Date,
    val description: String?,
    val user: User
)