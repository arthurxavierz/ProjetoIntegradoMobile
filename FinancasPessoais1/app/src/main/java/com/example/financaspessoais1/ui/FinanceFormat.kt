package com.example.financaspessoais1.ui

import com.example.financaspessoais1.model.Expense
import com.example.financaspessoais1.model.ExpenseCategory
import java.util.Locale

/** Categoria da despesa resolvida a partir de [Expense.categoryId]. */
val Expense.category: ExpenseCategory get() = ExpenseCategory.fromId(categoryId)

/** Rótulo curto da data ("20 Jun") derivado de [Expense.rawDate]. */
val Expense.dateLabel: String get() = dateLabelFromRaw(rawDate)

/** Meses abreviados, iguais ao protótipo. */
val MONTHS_ABBR = arrayOf(
    "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"
)

/** Meses por extenso. */
val MONTHS_FULL = arrayOf(
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
)

private fun mIndex(month: Int) = (month - 1).coerceIn(0, 11)

/** "Junho 2025" */
fun periodLabelFull(year: Int, month: Int) = "${MONTHS_FULL[mIndex(month)]} $year"

/** "JUNHO 2025" */
fun periodLabelUpper(year: Int, month: Int) = periodLabelFull(year, month).uppercase()

/** "Jun 2025" */
fun periodLabelShort(year: Int, month: Int) = "${MONTHS_ABBR[mIndex(month)]} $year"

/** true se a despesa (rawDate "YYYY-MM-DD") pertence ao período informado. */
fun Expense.inPeriod(year: Int, month: Int): Boolean {
    val prefix = "%04d-%02d".format(year, month)
    return rawDate.startsWith(prefix)
}

/** fmt(v) do protótipo: duas casas, vírgula decimal, sem separador de milhar. */
fun formatMoney(value: Double): String =
    String.format(Locale.US, "%.2f", value).replace('.', ',')

/** Data de hoje em ISO ("2026-06-25"). */
fun todayIso(): String {
    val c = java.util.Calendar.getInstance()
    return "%04d-%02d-%02d".format(
        c.get(java.util.Calendar.YEAR),
        c.get(java.util.Calendar.MONTH) + 1,
        c.get(java.util.Calendar.DAY_OF_MONTH)
    )
}

/**
 * Data padrão para uma nova despesa dentro do período exibido: hoje, se o período
 * for o mês corrente; senão o dia 1 do período (para a despesa cair no mês visto).
 */
fun defaultDateForPeriod(year: Int, month: Int): String {
    val c = java.util.Calendar.getInstance()
    val isCurrent = year == c.get(java.util.Calendar.YEAR) && month == c.get(java.util.Calendar.MONTH) + 1
    return if (isCurrent) todayIso() else "%04d-%02d-01".format(year, month)
}

/** "2025-06-20" -> "20 Jun" */
fun dateLabelFromRaw(raw: String): String {
    val parts = raw.split("-")
    return if (parts.size == 3) {
        val month = parts[1].toIntOrNull() ?: 1
        val day = parts[2].toIntOrNull() ?: 1
        "$day ${MONTHS_ABBR[(month - 1).coerceIn(0, 11)]}"
    } else raw
}
