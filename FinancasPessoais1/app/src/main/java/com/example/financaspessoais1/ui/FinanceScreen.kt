package com.example.financaspessoais1.ui

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.financaspessoais1.model.Expense
import com.example.financaspessoais1.model.ExpenseCategory

// Saver que permite ao rememberSaveable preservar a lista de despesas.
// Como Expense não é Parcelable, cada item é "achatado" em valores primitivos
// (Int, String, Double, String, Long) e remontado na restauração.
private val ExpenseListSaver: Saver<List<Expense>, Any> = listSaver(
    save = { list ->
        list.flatMap { e ->
            listOf(e.id, e.name, e.value, e.category.name, e.timestamp)
        }
    },
    restore = { flat ->
        flat.chunked(5).map { c ->
            Expense(
                id        = c[0] as Int,
                name      = c[1] as String,
                value     = c[2] as Double,
                category  = ExpenseCategory.valueOf(c[3] as String),
                timestamp = c[4] as Long
            )
        }
    }
)

@Composable
fun FinanceApp() {
    // rememberSaveable preserva o estado em mudanças de configuração
    // (ex.: rotação de tela) e morte do processo, sem precisar de banco de dados.
    var budget    by rememberSaveable { mutableStateOf(0.0) }
    var budgetSet by rememberSaveable { mutableStateOf(false) }
    var expenses  by rememberSaveable(stateSaver = ExpenseListSaver) { mutableStateOf(listOf<Expense>()) }
    var nextId    by rememberSaveable { mutableStateOf(1) }
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