package com.je.bizzumer.model

import java.util.*

data class Group(
    val id: Int,
    val name: String,
    val date: Date,
    val amountToPayByUser: Double,
    val comment: String?,
    val accessCode: String?,
    val ownerId: Int,
    val groupOwner: User,
    val participants: List<User>,
    val expenses: List<Expense>
)
