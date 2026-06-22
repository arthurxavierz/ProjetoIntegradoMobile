package com.example.financaspessoais1.ui

import androidx.compose.runtime.*
import com.example.financaspessoais1.model.Expense
import com.example.financaspessoais1.model.ExpenseCategory

@Composable
fun FinanceApp() {
    var budget    by remember { mutableDoubleStateOf(0.0) }
    var budgetSet by remember { mutableStateOf(false) }
    var expenses  by remember { mutableStateOf(listOf<Expense>()) }
    var nextId    by remember { mutableIntStateOf(1) }
    var showSheet by remember { mutableStateOf(false) }

    if (!budgetSet) {
        SetupScreen(
            onBudgetSet = { value ->
                budget    = value
                budgetSet = true
            }
        )
    } else {
        DashboardScreen(
            budget          = budget,
            expenses        = expenses,
            onAddExpense    = { showSheet = true },
            onDeleteExpense = { id -> expenses = expenses.filter { it.id != id } },
            onResetBudget   = {
                budget    = 0.0
                budgetSet = false
                expenses  = emptyList()
                nextId    = 1
            }
        )

        if (showSheet) {
            AddExpenseSheet(
                onDismiss = { showSheet = false },
                onAdd     = { name, value, category ->
                    expenses  = expenses + Expense(nextId++, name, value, category)
                    showSheet = false
                }
            )
        }
    }
}
