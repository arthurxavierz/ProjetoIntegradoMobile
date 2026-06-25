package com.example.financaspessoais1.data.db

import androidx.room.*
import com.example.financaspessoais1.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses WHERE ownerEmail = :owner ORDER BY createdAt DESC")
    fun getExpenses(owner: String): Flow<List<Expense>>

    @Query("SELECT COUNT(*) FROM expenses WHERE ownerEmail = :owner")
    suspend fun countForOwner(owner: String): Int

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Expense?

    @Insert
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)
}
