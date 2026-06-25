package com.example.gestaoacademica2.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Utilitários de data baseados em [Calendar]/[SimpleDateFormat] para suportar minSdk 24
 * (java.time exigiria desugaring). Datas são manipuladas como "yyyy-MM-dd".
 */
object DateUtils {

    private val ptBR = Locale("pt", "BR")
    private val ISO = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val MONTHS_SHORT = arrayOf(
        "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
        "Jul", "Ago", "Set", "Out", "Nov", "Dez"
    )
    private val MONTHS_LONG = arrayOf(
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )

    /** Data de hoje no formato yyyy-MM-dd. */
    fun today(): String = ISO.format(Date())

    /** Hoje deslocado de [days] dias, em yyyy-MM-dd. */
    fun todayPlus(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return ISO.format(cal.time)
    }

    private fun parse(iso: String): Calendar {
        val cal = Calendar.getInstance()
        try {
            cal.time = ISO.parse(iso) ?: Date()
        } catch (_: Exception) { /* mantém data atual */ }
        return cal
    }

    /** "24 Jun" — rótulo curto usado nos cartões. */
    fun shortLabel(iso: String): String {
        val cal = parse(iso)
        return "${cal.get(Calendar.DAY_OF_MONTH)} ${MONTHS_SHORT[cal.get(Calendar.MONTH)]}"
    }

    /** "23 de Junho de 2025" — saudação da aba Hoje. */
    fun longLabel(iso: String): String {
        val cal = parse(iso)
        return "${cal.get(Calendar.DAY_OF_MONTH)} de ${MONTHS_LONG[cal.get(Calendar.MONTH)]} de ${cal.get(Calendar.YEAR)}"
    }

    /** "Jun 2025" — badge do mês na aba Agenda. */
    fun monthBadge(iso: String): String {
        val cal = parse(iso)
        return "${MONTHS_SHORT[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}"
    }

    /** Converte yyyy-MM-dd em dd/MM/yyyy para exibição no formulário. */
    fun displayDate(iso: String): String {
        val cal = parse(iso)
        return SimpleDateFormat("dd/MM/yyyy", ptBR).format(cal.time)
    }

    /** Componentes (ano, mês 0-based, dia) de uma data ISO, para o DatePicker. */
    fun parts(iso: String): Triple<Int, Int, Int> {
        val cal = parse(iso)
        return Triple(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
    }

    /** Monta yyyy-MM-dd a partir dos componentes do DatePicker. */
    fun toIso(year: Int, month0: Int, day: Int): String {
        val cal = Calendar.getInstance()
        cal.set(year, month0, day)
        return ISO.format(cal.time)
    }
}
