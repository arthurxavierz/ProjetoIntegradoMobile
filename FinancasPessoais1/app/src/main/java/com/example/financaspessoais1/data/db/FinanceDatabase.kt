package com.example.financaspessoais1.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.financaspessoais1.model.Expense

@Database(
    entities = [Expense::class],
    version = 1,
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile private var INSTANCE: FinanceDatabase? = null

        fun getInstance(context: Context): FinanceDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FinanceDatabase::class.java,
                    "financas_pessoais.db"
                ).build().also { INSTANCE = it }
            }
    }
}
