package com.example.financaspessoais1.data

import android.content.Context
import androidx.core.content.edit
import java.security.MessageDigest

/**
 * Local-only account store (no backend). Credentials live in SharedPreferences
 * with the password kept as a SHA-256 hash rather than plain text.
 */
class AccountStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("financas_accounts", Context.MODE_PRIVATE)

    private fun key(email: String) = "acct_" + email.trim().lowercase()
    private fun nameKey(email: String) = "name_" + email.trim().lowercase()

    fun exists(email: String): Boolean = prefs.contains(key(email))

    fun register(email: String, password: String, name: String) {
        prefs.edit {
            putString(key(email), hash(password))
            putString(nameKey(email), name)
        }
    }

    fun authenticate(email: String, password: String): Boolean =
        prefs.getString(key(email), null) == hash(password)

    fun getName(email: String): String = prefs.getString(nameKey(email), "") ?: ""

    private fun hash(password: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
