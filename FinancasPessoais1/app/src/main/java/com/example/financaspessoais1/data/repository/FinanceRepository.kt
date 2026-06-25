package com.example.financaspessoais1.data.repository

import com.example.financaspessoais1.data.db.FinanceDatabase
import com.example.financaspessoais1.model.Expense
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val db: FinanceDatabase) {

    fun expenses(owner: String): Flow<List<Expense>> =
        db.expenseDao().getExpenses(owner)

    suspend fun countForOwner(owner: String): Int =
        db.expenseDao().countForOwner(owner)

    suspend fun getById(id: Long): Expense? =
        db.expenseDao().getById(id)

    suspend fun insert(expense: Expense): Long =
        db.expenseDao().insert(expense)

    suspend fun update(expense: Expense) =
        db.expenseDao().update(expense)

    suspend fun delete(expense: Expense) =
        db.expenseDao().delete(expense)
}
