package com.example.cicloestudos3.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** User-configurable app preferences surfaced on the Perfil screen. */
data class AppSettings(
    val revisionIntervalDays: Int = DEFAULT_INTERVAL_DAYS,
    val notificationsEnabled: Boolean = true,
    val loggedIn: Boolean = false,
    val userEmail: String = "",
    val userName: String = ""
) {
    companion object {
        const val DEFAULT_INTERVAL_DAYS = 2
        /** Selectable spaced-repetition intervals (in days). */
        val INTERVAL_OPTIONS = listOf(1, 2, 3, 5, 7)
    }
}

/** Lightweight SharedPreferences-backed settings store exposed as a [StateFlow]. */
class SettingsStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("estudos_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(
        AppSettings(
            revisionIntervalDays = prefs.getInt(KEY_INTERVAL, AppSettings.DEFAULT_INTERVAL_DAYS),
            notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS, true),
            loggedIn = prefs.getBoolean(KEY_LOGGED_IN, false),
            userEmail = prefs.getString(KEY_USER_EMAIL, "") ?: "",
            userName = prefs.getString(KEY_USER_NAME, "") ?: ""
        )
    )
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun login(email: String, name: String) {
        prefs.edit {
            putBoolean(KEY_LOGGED_IN, true)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
        }
        _settings.value = _settings.value.copy(loggedIn = true, userEmail = email, userName = name)
    }

    fun logout() {
        prefs.edit {
            putBoolean(KEY_LOGGED_IN, false)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_NAME)
        }
        _settings.value = _settings.value.copy(loggedIn = false, userEmail = "", userName = "")
    }

    fun setRevisionIntervalDays(days: Int) {
        prefs.edit { putInt(KEY_INTERVAL, days) }
        _settings.value = _settings.value.copy(revisionIntervalDays = days)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_NOTIFICATIONS, enabled) }
        _settings.value = _settings.value.copy(notificationsEnabled = enabled)
    }

    companion object {
        private const val KEY_INTERVAL = "revision_interval_days"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
    }
}
