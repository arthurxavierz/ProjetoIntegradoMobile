package com.example.gestaoacademica2.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Sessão + preferências do usuário exibidas na aba Perfil. */
data class AppSettings(
    val loggedIn: Boolean = false,
    val userEmail: String = "",
    val userName: String = "",
    val course: String = DEFAULT_COURSE,
    val semester: String = DEFAULT_SEMESTER
) {
    companion object {
        const val DEFAULT_COURSE = "Engenharia de Software"
        const val DEFAULT_SEMESTER = "2025/1"
    }
}

/**
 * Store de sessão/preferências em SharedPreferences exposto como [StateFlow].
 * Espelha o SettingsStore dos apps irmãos: a sessão persiste entre execuções e o
 * curso/semestre são guardados por usuário (chaveados pelo e-mail).
 */
class SettingsStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("academica_settings", Context.MODE_PRIVATE)

    private fun courseKey(email: String) = "course_" + email.trim().lowercase()
    private fun semesterKey(email: String) = "semester_" + email.trim().lowercase()

    private val _settings = MutableStateFlow(loadInitial())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadInitial(): AppSettings {
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        return AppSettings(
            loggedIn = prefs.getBoolean(KEY_LOGGED_IN, false),
            userEmail = email,
            userName = prefs.getString(KEY_USER_NAME, "") ?: "",
            course = prefs.getString(courseKey(email), AppSettings.DEFAULT_COURSE) ?: AppSettings.DEFAULT_COURSE,
            semester = prefs.getString(semesterKey(email), AppSettings.DEFAULT_SEMESTER) ?: AppSettings.DEFAULT_SEMESTER
        )
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
            course = prefs.getString(courseKey(email), AppSettings.DEFAULT_COURSE) ?: AppSettings.DEFAULT_COURSE,
            semester = prefs.getString(semesterKey(email), AppSettings.DEFAULT_SEMESTER) ?: AppSettings.DEFAULT_SEMESTER
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

    fun setCourse(value: String) {
        val email = _settings.value.userEmail
        if (email.isBlank()) return
        prefs.edit { putString(courseKey(email), value) }
        _settings.value = _settings.value.copy(course = value)
    }

    fun setSemester(value: String) {
        val email = _settings.value.userEmail
        if (email.isBlank()) return
        prefs.edit { putString(semesterKey(email), value) }
        _settings.value = _settings.value.copy(semester = value)
    }

    companion object {
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
    }
}
