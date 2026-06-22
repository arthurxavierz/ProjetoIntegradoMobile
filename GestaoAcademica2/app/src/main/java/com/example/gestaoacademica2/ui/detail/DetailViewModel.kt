package com.example.gestaoacademica2.ui.detail

import androidx.lifecycle.*
import com.example.gestaoacademica2.data.model.AcademicEvent
import com.example.gestaoacademica2.data.repository.EventRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val repo: EventRepository) : ViewModel() {

    private val _event = MutableLiveData<AcademicEvent?>()
    val event: LiveData<AcademicEvent?> = _event

    fun loadEvent(id: Int) = viewModelScope.launch {
        _event.postValue(repo.getById(id))
    }

    fun delete() = viewModelScope.launch {
        _event.value?.let { repo.delete(it) }
    }

    fun toggleFavorite() = viewModelScope.launch {
        val e = _event.value ?: return@launch
        repo.setFavorite(e.id, !e.isFavorite)
        _event.postValue(e.copy(isFavorite = !e.isFavorite))
    }

    fun toggleCompleted() = viewModelScope.launch {
        val e = _event.value ?: return@launch
        repo.setCompleted(e.id, !e.isCompleted)
        _event.postValue(e.copy(isCompleted = !e.isCompleted))
    }

    class Factory(private val repo: EventRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetailViewModel(repo) as T
    }
}
