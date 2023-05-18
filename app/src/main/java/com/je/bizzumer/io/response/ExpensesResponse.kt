package com.je.bizzumer.io.response

import com.je.bizzumer.model.Expense

data class ExpenseCalculationResult(
    val transactions: List<Transaction>
)

data class Transaction(
    val from: String,
    val to: String,
    val amount: Double
)

data class ExpensesList(
    val expenses: List<Expense>
)