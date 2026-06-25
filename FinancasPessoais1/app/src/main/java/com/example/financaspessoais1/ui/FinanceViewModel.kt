package com.example.financaspessoais1.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financaspessoais1.data.AccountStore
import com.example.financaspessoais1.data.AppSettings
import com.example.financaspessoais1.data.SettingsStore
import com.example.financaspessoais1.data.repository.FinanceRepository
import com.example.financaspessoais1.model.Expense
import com.example.financaspessoais1.model.ExpenseCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Single ViewModel backing the four Finanças tabs + login gate. Mirrors the
 * CicloEstudos3 architecture: Room-backed data exposed as [StateFlow], local
 * accounts via [AccountStore], session/budget via [SettingsStore]. All reads and
 * writes are scoped to the signed-in user's e-mail ([Expense.ownerEmail]).
 */
class FinanceViewModel(
    private val repo: FinanceRepository,
    private val appContext: Context
) : ViewModel() {

    private val settingsStore = SettingsStore(appContext)
    private val accountStore = AccountStore(appContext)

    val settings: StateFlow<AppSettings> = settingsStore.settings

    private val owner: String get() = settingsStore.settings.value.userEmail

    /** Despesas do usuário logado, recalculadas sempre que o usuário muda. */
    @OptIn(ExperimentalCoroutinesApi::class)
    val expenses: StateFlow<List<Expense>> = settingsStore.settings
        .map { it.userEmail }
        .distinctUntilChanged()
        .flatMapLatest { email -> repo.expenses(email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Expense CRUD ──────────────────────────────────────────────────────────

    fun addExpense(name: String, value: Double, category: ExpenseCategory, rawDate: String) =
        viewModelScope.launch {
            repo.insert(
                Expense(
                    name = name.trim(),
                    categoryId = category.id,
                    value = value,
                    rawDate = rawDate,
                    ownerEmail = owner
                )
            )
        }

    fun updateExpense(id: Long, name: String, value: Double, category: ExpenseCategory, rawDate: String) =
        viewModelScope.launch {
            val current = repo.getById(id) ?: return@launch
            repo.update(
                current.copy(
                    name = name.trim(),
                    categoryId = category.id,
                    value = value,
                    rawDate = rawDate
                )
            )
        }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repo.delete(expense)
    }

    fun deleteExpenseById(id: Long) = viewModelScope.launch {
        repo.getById(id)?.let { repo.delete(it) }
    }

    // ── Budget (teto mensal) ──────────────────────────────────────────────────

    fun setMonthlyBudget(value: Double) = settingsStore.setMonthlyBudget(value)

    // ── Período (mês/ano exibido) ─────────────────────────────────────────────

    fun setPeriod(year: Int, month: Int) = settingsStore.setPeriod(year, month)

    // ── Auth (local accounts, no backend) ─────────────────────────────────────

    /** Returns an error message, or null on success (session is marked signed in). */
    fun login(email: String, password: String): String? {
        val e = email.trim()
        if (!accountStore.exists(e)) return "Nenhuma conta encontrada com esse e-mail."
        if (!accountStore.authenticate(e, password)) return "Senha incorreta."
        settingsStore.login(e, accountStore.getName(e))
        return null
    }

    /** Creates a local account and signs in. New accounts start empty. */
    fun register(name: String, email: String, password: String): String? {
        val e = email.trim()
        if (accountStore.exists(e)) return "Já existe uma conta com esse e-mail."
        accountStore.register(e, password, name.trim())
        settingsStore.login(e, name.trim())
        return null
    }

    fun logout() = settingsStore.logout()

    class Factory(
        private val repo: FinanceRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FinanceViewModel(repo, context.applicationContext) as T
    }
}
