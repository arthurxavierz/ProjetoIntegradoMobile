package com.example.financaspessoais1

import android.app.Application
import com.example.financaspessoais1.data.db.FinanceDatabase
import com.example.financaspessoais1.data.repository.FinanceRepository

class FinancasApp : Application() {
    val database: FinanceDatabase by lazy { FinanceDatabase.getInstance(this) }
    val repository: FinanceRepository by lazy { FinanceRepository(database) }
}
