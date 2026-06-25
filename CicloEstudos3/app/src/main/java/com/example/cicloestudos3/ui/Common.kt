package com.example.cicloestudos3.ui

import androidx.compose.ui.graphics.Color
import com.example.cicloestudos3.ui.theme.EstudosPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val ptBR = Locale("pt", "BR")
private val monthsShort = arrayOf(
    "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
    "Jul", "Ago", "Set", "Out", "Nov", "Dez"
)

fun parseColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) {
    EstudosPrimary
}

/** "22 Jun" style label used across the design. */
fun shortDate(ms: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = ms }
    return "${cal.get(Calendar.DAY_OF_MONTH)} ${monthsShort[cal.get(Calendar.MONTH)]}"
}

/** Eyebrow label for the home header, e.g. "JUNHO 2025". */
fun monthYearEyebrow(ms: Long = System.currentTimeMillis()): String {
    val cal = Calendar.getInstance().apply { timeInMillis = ms }
    val full = arrayOf(
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )
    return "${full[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}".uppercase(ptBR)
}

fun monthLabel(ms: Long = System.currentTimeMillis()): String {
    val cal = Calendar.getInstance().apply { timeInMillis = ms }
    val full = arrayOf(
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )
    return full[cal.get(Calendar.MONTH)]
}

private fun midnight(ms: Long): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = ms
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

fun daysUntil(scheduledAt: Long): Int {
    val diff = midnight(scheduledAt) - midnight(System.currentTimeMillis())
    return Math.round(diff / 86_400_000.0).toInt()
}

/** "Hoje" / "Amanhã" / "em 3d" pill label. */
fun daysLabel(scheduledAt: Long): String = when (val d = daysUntil(scheduledAt)) {
    0 -> "Hoje"
    1 -> "Amanhã"
    in Int.MIN_VALUE..-1 -> "Atrasada"
    else -> "em ${d}d"
}

private val dmyFmt = SimpleDateFormat("dd/MM/yyyy", ptBR)
fun fullDate(ms: Long): String = dmyFmt.format(Date(ms))
