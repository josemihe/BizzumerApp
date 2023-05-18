package com.je.bizzumer.model

import java.util.*

data class GroupUser(
    val groupId: Int,
    val userId: Int,
    val createdAt: String,
    val updatedAt: Date,
    val paid: Double,
    val inGroup: Group,
    val isUser: User,
    val expenses: List<Expense>
)
