package com.example.gestaoacademica2.ui.addedit

import androidx.lifecycle.*
import com.example.gestaoacademica2.data.model.AcademicEvent
import com.example.gestaoacademica2.data.model.EventCategory
import com.example.gestaoacademica2.data.model.Priority
import com.example.gestaoacademica2.data.repository.EventRepository
import kotlinx.coroutines.launch

class AddEditViewModel(private val repo: EventRepository) : ViewModel() {

    // Estado do formulário (LiveData para two-way binding via Fragment)
    val title       = MutableLiveData("")
    val subject     = MutableLiveData("")
    val description = MutableLiveData("")
    val location    = MutableLiveData("")
    val category    = MutableLiveData<EventCategory?>(null)
    val priority    = MutableLiveData(Priority.MEDIA)
    val date        = MutableLiveData<Long?>(null)

    private var editingId = 0

    val isEditing: Boolean get() = editingId != 0

    /** Carrega evento existente para edição. */
    fun loadEvent(id: Int) = viewModelScope.launch {
        val event = repo.getById(id) ?: return@launch
        editingId       = event.id
        title.value     = event.title
        subject.value   = event.subject
        description.value = event.description
        location.value  = event.location
        category.value  = event.category
        priority.value  = event.priority
        date.value      = event.date
    }

    /**
     * Valida e salva. Retorna null em caso de erro, ou uma mensagem de erro.
     * Retorna "" (empty) quando salvo com sucesso.
     */
    fun save(): String? {
        val titleVal    = title.value?.trim() ?: ""
        val categoryVal = category.value
        val dateVal     = date.value

        if (titleVal.isBlank()) return "title"
        if (categoryVal == null) return "category"
        if (dateVal == null) return "date"

        val event = AcademicEvent(
            id          = editingId,
            title       = titleVal,
            subject     = subject.value?.trim() ?: "",
            description = description.value?.trim() ?: "",
            location    = location.value?.trim() ?: "",
            category    = categoryVal,
            priority    = priority.value ?: Priority.MEDIA,
            date        = dateVal
        )

        viewModelScope.launch { repo.upsert(event) }
        return null  // sucesso
    }

    class Factory(private val repo: EventRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AddEditViewModel(repo) as T
    }
}
