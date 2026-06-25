package com.example.gestaoacademica2.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gestaoacademica2.data.AccountStore
import com.example.gestaoacademica2.data.AppSettings
import com.example.gestaoacademica2.data.SettingsStore
import com.example.gestaoacademica2.data.model.AcademicEvent
import com.example.gestaoacademica2.data.model.EventType
import com.example.gestaoacademica2.data.repository.EventRepository
import com.example.gestaoacademica2.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel único das quatro abas (Hoje/Agenda/Favoritos/Perfil) + porta de login.
 * Espelha a arquitetura dos apps irmãos: dados via Room expostos como [StateFlow],
 * contas locais via [AccountStore] e sessão/preferências via [SettingsStore].
 */
class MainViewModel(
    private val repo: EventRepository,
    appContext: Context
) : ViewModel() {

    private val settingsStore = SettingsStore(appContext)
    private val accountStore = AccountStore(appContext)

    val settings: StateFlow<AppSettings> = settingsStore.settings

    // ── Filtro da Agenda ──
    private val _filter = MutableStateFlow<EventType?>(null)   // null = "Todos"
    val filter: StateFlow<EventType?> = _filter.asStateFlow()

    // ── Eventos (escopo do usuário logado) ──
    @OptIn(ExperimentalCoroutinesApi::class)
    val allEvents: StateFlow<List<AcademicEvent>> = settings
        .map { it.userEmail }
        .distinctUntilChanged()
        .flatMapLatest { email -> if (email.isBlank()) flowOf(emptyList()) else repo.events(email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todayEvents: StateFlow<List<AcademicEvent>> =
        allEvents.mapToState { list -> list.filter { it.date == DateUtils.today() } }

    val upcomingEvents: StateFlow<List<AcademicEvent>> =
        allEvents.mapToState { list -> list.filter { it.date > DateUtils.today() }.take(6) }

    val favoriteEvents: StateFlow<List<AcademicEvent>> =
        allEvents.mapToState { list -> list.filter { it.isFavorite } }

    val filteredEvents: StateFlow<List<AcademicEvent>> =
        combine(allEvents, _filter) { list, f ->
            if (f == null) list else list.filter { it.type == f }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private fun Flow<List<AcademicEvent>>.mapToState(
        transform: (List<AcademicEvent>) -> List<AcademicEvent>
    ): StateFlow<List<AcademicEvent>> =
        map { transform(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Filtro ──
    fun setFilter(type: EventType?) { _filter.value = type }

    // ── Auth (contas locais, sem backend) ──

    /** Retorna mensagem de erro, ou null em caso de sucesso (sessão marcada como logada). */
    fun login(email: String, password: String): String? {
        val e = email.trim()
        if (!accountStore.exists(e)) return "Nenhuma conta encontrada com esse e-mail."
        if (!accountStore.authenticate(e, password)) return "Senha incorreta."
        settingsStore.login(e, accountStore.getName(e))
        return null
    }

    /** Cria uma conta local e entra. */
    fun register(name: String, email: String, password: String): String? {
        val e = email.trim()
        if (accountStore.exists(e)) return "Já existe uma conta com esse e-mail."
        accountStore.register(e, password, name.trim())
        settingsStore.login(e, name.trim())
        return null
    }

    fun logout() {
        settingsStore.logout()
        _filter.value = null
    }

    fun setCourse(value: String) = settingsStore.setCourse(value)
    fun setSemester(value: String) = settingsStore.setSemester(value)

    // ── CRUD ──
    fun addEvent(name: String, type: EventType, date: String, time: String, location: String) =
        viewModelScope.launch {
            repo.insert(
                AcademicEvent(
                    name = name, type = type, date = date, time = time,
                    location = location, isFavorite = false,
                    ownerEmail = settings.value.userEmail
                )
            )
        }

    fun updateEvent(event: AcademicEvent) = viewModelScope.launch { repo.update(event) }

    fun deleteEvent(id: Long) = viewModelScope.launch { repo.deleteById(id) }

    fun toggleFavorite(event: AcademicEvent) = viewModelScope.launch {
        repo.setFavorite(event.id, !event.isFavorite)
    }

    class Factory(
        private val repo: EventRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MainViewModel(repo, context.applicationContext) as T
    }
}
