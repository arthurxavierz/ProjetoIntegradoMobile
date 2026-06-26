package com.example.financaspessoais1.model

/**
 * Resultado consolidado do orçamento em um dado momento.
 */
data class BudgetSummary(
    val budget: Double,
    val spent: Double,
    val remaining: Double,
    val progress: Float,      // fração de 0f a 1f (limitada), usada na barra de progresso
    val percentUsed: Int,     // percentual exibido (pode passar de 100)
    val isOverBudget: Boolean
)

/**
 * Centraliza os cálculos de orçamento em uma função pura, sem dependência de UI.
 * Isso deixa a lógica de "total consumido" e "saldo restante" testável por
 * testes unitários (JUnit), em vez de ficar espalhada dentro dos Composables.
 */
object FinanceCalculations {

    fun summarize(budget: Double, expenses: List<Expense>): BudgetSummary {
        val spent       = expenses.sumOf { it.value }
        val remaining   = budget - spent
        val progress    = if (budget > 0) (spent / budget).coerceIn(0.0, 1.0).toFloat() else 0f
        val percentUsed = if (budget > 0) ((spent / budget) * 100).toInt() else 0
        return BudgetSummary(
            budget       = budget,
            spent        = spent,
            remaining    = remaining,
            progress     = progress,
            percentUsed  = percentUsed,
            isOverBudget = remaining < 0
        )
    }
}