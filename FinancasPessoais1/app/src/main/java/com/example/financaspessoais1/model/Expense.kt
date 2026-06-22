package com.example.financaspessoais1.model

data class Expense(
    val id: Int,
    val name: String,
    val value: Double,
    val category: ExpenseCategory,
    val timestamp: Long = System.currentTimeMillis()
)
