package com.example.financaspessoais1.data

import android.content.Context
import androidx.core.content.edit
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Session + per-user preferences surfaced on the Perfil screen. */
data class AppSettings(
    val loggedIn: Boolean = false,
    val userEmail: String = "",
    val userName: String = "",
    val monthlyBudget: Double = DEFAULT_BUDGET,
    val periodYear: Int = currentYear(),
    val periodMonth: Int = currentMonth()   // 1..12
) {
    companion object {
        const val DEFAULT_BUDGET = 2000.0
        fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)
        fun currentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    }
}

/** SharedPreferences-backed settings store exposed as a [StateFlow]. */
class SettingsStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("financas_settings", Context.MODE_PRIVATE)

    private fun budgetKey(email: String) = "budget_" + email.trim().lowercase()
    private fun periodYearKey(email: String) = "period_y_" + email.trim().lowercase()
    private fun periodMonthKey(email: String) = "period_m_" + email.trim().lowercase()

    private val _settings = MutableStateFlow(loadInitial())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadInitial(): AppSettings {
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        return AppSettings(
            loggedIn = prefs.getBoolean(KEY_LOGGED_IN, false),
            userEmail = email,
            userName = prefs.getString(KEY_USER_NAME, "") ?: "",
            monthlyBudget = budgetFor(email),
            periodYear = prefs.getInt(periodYearKey(email), AppSettings.currentYear()),
            periodMonth = prefs.getInt(periodMonthKey(email), AppSettings.currentMonth())
        )
    }

    private fun budgetFor(email: String): Double {
        if (email.isBlank()) return AppSettings.DEFAULT_BUDGET
        val bits = prefs.getLong(budgetKey(email), java.lang.Double.doubleToRawLongBits(AppSettings.DEFAULT_BUDGET))
        return java.lang.Double.longBitsToDouble(bits)
    }

    fun login(email: String, name: String) {
        prefs.edit {
            putBoolean(KEY_LOGGED_IN, true)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
        }
        _settings.value = AppSettings(
            loggedIn = true,
            userEmail = email,
            userName = name,
            monthlyBudget = budgetFor(email),
            periodYear = prefs.getInt(periodYearKey(email), AppSettings.currentYear()),
            periodMonth = prefs.getInt(periodMonthKey(email), AppSettings.currentMonth())
        )
    }

    fun logout() {
        prefs.edit {
            putBoolean(KEY_LOGGED_IN, false)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_NAME)
        }
        _settings.value = AppSettings()
    }

    fun setMonthlyBudget(value: Double) {
        val email = _settings.value.userEmail
        if (email.isBlank()) return
        prefs.edit { putLong(budgetKey(email), java.lang.Double.doubleToRawLongBits(value)) }
        _settings.value = _settings.value.copy(monthlyBudget = value)
    }

    fun setPeriod(year: Int, month: Int) {
        val email = _settings.value.userEmail
        if (email.isBlank()) return
        prefs.edit {
            putInt(periodYearKey(email), year)
            putInt(periodMonthKey(email), month)
        }
        _settings.value = _settings.value.copy(periodYear = year, periodMonth = month)
    }

    companion object {
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
    }
}
