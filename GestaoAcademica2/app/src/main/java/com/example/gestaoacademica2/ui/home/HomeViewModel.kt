package com.example.gestaoacademica2.ui.home

import androidx.lifecycle.*
import com.example.gestaoacademica2.data.model.AcademicEvent
import com.example.gestaoacademica2.data.model.EventCategory
import com.example.gestaoacademica2.data.repository.EventRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(private val repo: EventRepository) : ViewModel() {

    private val _selectedCategory  = MutableStateFlow<EventCategory?>(null)
    private val _showFavoritesOnly = MutableStateFlow(false)
    private val _searchQuery       = MutableStateFlow("")

    /** Lista filtrada e ordenada — observada pelo Fragment via collectLatest. */
    val events: StateFlow<List<AcademicEvent>> = combine(
        repo.allEvents,
        _selectedCategory,
        _showFavoritesOnly,
        _searchQuery
    ) { all, category, favOnly, query ->
        all
            .filter { event -> category == null || event.category == category }
            .filter { event -> !favOnly || event.isFavorite }
            .filter { event ->
                query.isBlank() ||
                event.title.contains(query, ignoreCase = true) ||
                event.subject.contains(query, ignoreCase = true)
            }
            .sortedBy { it.date }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Ações de filtro ──────────────────────────────────────────────────────

    fun filterByCategory(category: EventCategory?) {
        _selectedCategory.value  = category
        _showFavoritesOnly.value = false
    }

    fun filterFavorites(enabled: Boolean) {
        _showFavoritesOnly.value = enabled
        if (enabled) _selectedCategory.value = null
    }

    fun setSearch(query: String) {
        _searchQuery.value = query
    }

    fun clearFilters() {
        _selectedCategory.value  = null
        _showFavoritesOnly.value = false
        _searchQuery.value       = ""
    }

    // ── Ações de dados ───────────────────────────────────────────────────────

    fun toggleFavorite(event: AcademicEvent) = viewModelScope.launch {
        repo.setFavorite(event.id, !event.isFavorite)
    }

    fun delete(event: AcademicEvent) = viewModelScope.launch {
        repo.delete(event)
    }

    // ── Factory ─────────────────────────────────────────────────────────────

    class Factory(private val repo: EventRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(repo) as T
    }
}
