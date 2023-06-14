package com.je.bizzumer.io.response

import com.je.bizzumer.model.Expense
import com.je.bizzumer.model.User

data class ExpenseCalculationResult(
    val transactions: List<Transaction>,
    val user: User
)

data class Transaction(
    val from: String,
    val to: String,
    val amount: Double,
)

data class ExpensesList(
    val expenses: List<Expense>
)