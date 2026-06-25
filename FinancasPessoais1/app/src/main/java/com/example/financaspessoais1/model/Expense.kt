package com.example.financaspessoais1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Uma despesa persistida no Room. [categoryId] referencia [ExpenseCategory.id],
 * [rawDate] é a data ISO ("2025-06-20") e [ownerEmail] isola os dados por usuário.
 */
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val categoryId: String,
    val value: Double,
    val rawDate: String,
    val ownerEmail: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
