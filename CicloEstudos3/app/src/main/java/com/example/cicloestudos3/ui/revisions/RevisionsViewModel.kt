package com.example.cicloestudos3.ui.revisions

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cicloestudos3.data.model.Revision
import com.example.cicloestudos3.data.model.Subject
import com.example.cicloestudos3.data.model.Topic
import com.example.cicloestudos3.data.model.TopicWithSubject
import com.example.cicloestudos3.data.repository.StudyRepository
import com.example.cicloestudos3.worker.RevisionReminderWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class RevisionsViewModel(
    private val repo: StudyRepository,
    private val context: Context
) : ViewModel() {

    val allRevisions: StateFlow<List<Revision>> =
        repo.allRevisions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allSubjects: StateFlow<List<Subject>> =
        repo.allSubjects.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recentTopics: StateFlow<List<TopicWithSubject>> =
        repo.getRecentTopics(50).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun scheduleRevision(
        topicId: Int,
        subjectId: Int,
        topicTitle: String,
        subjectName: String,
        subjectColorHex: String,
        scheduledAt: Long,
        notifyEnabled: Boolean
    ) = viewModelScope.launch {
        val revision = Revision(
            topicId         = topicId,
            subjectId       = subjectId,
            topicTitle      = topicTitle,
            subjectName     = subjectName,
            subjectColorHex = subjectColorHex,
            scheduledAt     = scheduledAt
        )
        val id = repo.insertRevision(revision).toInt()

        if (notifyEnabled) {
            val workerId = RevisionReminderWorker.schedule(
                context, id, topicTitle, subjectName, scheduledAt
            ).toString()
            repo.updateRevision(revision.copy(id = id, workerRequestId = workerId))
        }
    }

    fun toggleCompleted(revision: Revision) = viewModelScope.launch {
        val newCompleted = !revision.isCompleted
        repo.setRevisionCompleted(revision.id, newCompleted)
        if (newCompleted && revision.workerRequestId.isNotBlank()) {
            RevisionReminderWorker.cancel(context, revision.id)
        }
    }

    fun deleteRevision(revision: Revision) = viewModelScope.launch {
        if (revision.workerRequestId.isNotBlank()) {
            RevisionReminderWorker.cancel(context, revision.id)
        }
        repo.deleteRevision(revision)
    }

    class Factory(private val repo: StudyRepository, private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            RevisionsViewModel(repo, context) as T
    }
}

/** Groups revisions by date for display */
fun List<Revision>.groupByDate(): Map<String, List<Revision>> {
    val fmt = java.text.SimpleDateFormat("EEE, dd/MM/yyyy", java.util.Locale("pt", "BR"))
    return groupBy { fmt.format(java.util.Date(it.scheduledAt)) }
        .toSortedMap(compareBy { key ->
            groupKeys(this, key)
        })
}

private fun groupKeys(revisions: List<Revision>, key: String): Long {
    return revisions.firstOrNull {
        val fmt = java.text.SimpleDateFormat("EEE, dd/MM/yyyy", java.util.Locale("pt", "BR"))
        fmt.format(java.util.Date(it.scheduledAt)) == key
    }?.scheduledAt ?: 0L
}
