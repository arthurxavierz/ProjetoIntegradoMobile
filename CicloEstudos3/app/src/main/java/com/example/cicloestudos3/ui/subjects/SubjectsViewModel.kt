package com.example.cicloestudos3.ui.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cicloestudos3.data.model.Subject
import com.example.cicloestudos3.data.model.SubjectWithStats
import com.example.cicloestudos3.data.model.Topic
import com.example.cicloestudos3.data.model.TopicWithSubject
import com.example.cicloestudos3.data.repository.StudyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SubjectsViewModel(private val repo: StudyRepository) : ViewModel() {

    val subjectsWithStats: StateFlow<List<SubjectWithStats>> =
        repo.subjectsWithStats.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun insertSubject(name: String, colorHex: String) = viewModelScope.launch {
        repo.insertSubject(Subject(name = name.trim(), colorHex = colorHex))
    }

    fun deleteSubject(subject: Subject) = viewModelScope.launch {
        repo.deleteSubject(subject)
    }

    class Factory(private val repo: StudyRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = SubjectsViewModel(repo) as T
    }
}

class SubjectDetailViewModel(
    private val repo: StudyRepository,
    private val subjectId: Int
) : ViewModel() {

    val topics: StateFlow<List<TopicWithSubject>> =
        repo.getTopicsForSubject(subjectId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun insertTopic(title: String, notes: String, durationMinutes: Int, studiedAt: Long) =
        viewModelScope.launch {
            repo.insertTopic(
                Topic(
                    subjectId       = subjectId,
                    title           = title.trim(),
                    notes           = notes.trim(),
                    durationMinutes = durationMinutes,
                    studiedAt       = studiedAt
                )
            )
        }

    fun deleteTopic(topic: com.example.cicloestudos3.data.model.Topic) = viewModelScope.launch {
        repo.deleteTopic(topic)
    }

    class Factory(private val repo: StudyRepository, private val subjectId: Int) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SubjectDetailViewModel(repo, subjectId) as T
    }
}
