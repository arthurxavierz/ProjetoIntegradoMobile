package com.example.cicloestudos3.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cicloestudos3.data.model.Revision
import com.example.cicloestudos3.data.model.Subject
import com.example.cicloestudos3.data.model.SubjectWithStats
import com.example.cicloestudos3.data.model.Topic
import com.example.cicloestudos3.data.model.TopicWithSubject
import com.example.cicloestudos3.data.AccountStore
import com.example.cicloestudos3.data.AppSettings
import com.example.cicloestudos3.data.SettingsStore
import com.example.cicloestudos3.data.repository.StudyRepository
import com.example.cicloestudos3.worker.RevisionReminderWorker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Single ViewModel backing all four Estudos tabs.
 *
 * Schema mapping (prototype mock -> real Room entities):
 *  - "discipline" -> [com.example.cicloestudos3.data.model.Subject] (+ aggregated topics)
 *  - "session"    -> [Topic] (subjectId->disc, title->topic, studiedAt->date,
 *                    durationMinutes->duration, notes->notes)
 *  - "revision"   -> [Revision]
 *
 * Registering a session (Topic) auto-schedules a spaced-repetition [Revision]
 * via WorkManager so the existing reminder pipeline keeps working.
 */
data class StudyUiState(
    val subjects: List<SubjectWithStats> = emptyList(),
    val sessions: List<TopicWithSubject> = emptyList(),
    val revisions: List<Revision> = emptyList(),
    val upcomingRevisions: List<Revision> = emptyList()
) {
    val totalSessions: Int get() = sessions.size
    val totalMinutes: Int get() = sessions.sumOf { it.topic.durationMinutes }
    val totalHours: Int get() = totalMinutes / 60
    val totalDisciplines: Int get() = subjects.size
    val pendingRevisions: List<Revision> get() = revisions.filter { !it.isCompleted }
}

/** Form model for the add/edit-session bottom sheet. */
data class SessionForm(
    val editingTopicId: Int? = null,
    val disc: String = "",
    val topic: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val duration: String = "60",
    val notes: String = ""
) {
    val isEdit: Boolean get() = editingTopicId != null
}

/** Form model for the add/edit-discipline bottom sheet. */
data class SubjectForm(
    val editingSubjectId: Int? = null,
    val name: String = "",
    val colorHex: String = com.example.cicloestudos3.ui.theme.subjectColorPalette.first()
) {
    val isEdit: Boolean get() = editingSubjectId != null
}

/** Form model for the add/edit-revision bottom sheet. */
data class RevisionForm(
    val editingRevisionId: Int? = null,
    val subjectId: Int? = null,
    val topicId: Int? = null,
    val subjectName: String = "",
    val topicTitle: String = "",
    val scheduledAt: Long = defaultRevisionTime()
) {
    val isEdit: Boolean get() = editingRevisionId != null
}

private fun defaultRevisionTime(): Long = Calendar.getInstance().apply {
    add(Calendar.DAY_OF_YEAR, REVISION_OFFSET_DAYS)
    set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis

/** Default spaced-repetition offset for auto-scheduled revisions. */
private const val REVISION_OFFSET_DAYS = 2

class StudyViewModel(
    private val repo: StudyRepository,
    private val appContext: Context
) : ViewModel() {

    private val settingsStore = SettingsStore(appContext)
    private val accountStore = AccountStore(appContext)

    /** User preferences (revision interval, notifications) backing the Perfil screen. */
    val settings: StateFlow<AppSettings> = settingsStore.settings

    /** The currently signed-in user's email; all reads/writes are scoped to it. */
    private val owner: String get() = settingsStore.settings.value.userEmail

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<StudyUiState> = settingsStore.settings
        .map { it.userEmail }
        .distinctUntilChanged()
        .flatMapLatest { email ->
            combine(
                repo.subjectsWithStats(email),
                repo.allTopicsWithSubject(email),
                repo.allRevisions(email),
                repo.getUpcomingRevisions(email, 5)
            ) { subjects, sessions, revisions, upcoming ->
                StudyUiState(
                    subjects = subjects,
                    sessions = sessions,
                    revisions = revisions,
                    upcomingRevisions = upcoming
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StudyUiState())

    // ── Discipline color assignment ─────────────────────────────────────────────

    private fun nextColorFor(name: String, knownCount: Int): String {
        val palette = com.example.cicloestudos3.ui.theme.subjectColorPalette
        return palette[knownCount % palette.size]
    }

    // ── Subject (Disciplina) CRUD ────────────────────────────────────────────────

    /**
     * Result of attempting to save a discipline. UI uses this for inline validation.
     */
    sealed interface SaveResult {
        data object Success : SaveResult
        data class Error(val message: String) : SaveResult
    }

    /**
     * Creates or renames/recolors a discipline. Rejects blank or duplicate names.
     * On edit, denormalized name/color on existing revisions are kept in sync.
     */
    fun saveSubject(form: SubjectForm, onResult: (SaveResult) -> Unit = {}) = viewModelScope.launch {
        val name = form.name.trim()
        if (name.isEmpty()) {
            onResult(SaveResult.Error("Nome é obrigatório.")); return@launch
        }
        val editId = form.editingSubjectId
        val clash = if (editId == null) repo.getSubjectByName(name, owner)
                    else repo.getSubjectByNameExcluding(name, editId, owner)
        if (clash != null) {
            onResult(SaveResult.Error("Já existe uma disciplina com esse nome.")); return@launch
        }
        if (editId == null) {
            repo.insertSubject(Subject(name = name, colorHex = form.colorHex, ownerEmail = owner))
        } else {
            val current = repo.getSubjectById(editId)
            repo.updateSubject(
                Subject(
                    id = editId,
                    name = name,
                    colorHex = form.colorHex,
                    ownerEmail = owner,
                    createdAt = current?.createdAt ?: System.currentTimeMillis()
                )
            )
            // Keep denormalized revision display fields consistent.
            repo.updateRevisionSubjectInfo(editId, name, form.colorHex)
        }
        onResult(SaveResult.Success)
    }

    /**
     * Deletes a discipline. Topics cascade-delete via the Room FK; revisions have
     * no FK (denormalized), so we delete them manually and cancel their reminders
     * to keep WorkManager and DB integrity.
     */
    fun deleteSubject(subject: Subject) = viewModelScope.launch {
        val orphanRevisions = repo.getRevisionsBySubjectId(subject.id)
        orphanRevisions.forEach { rev ->
            RevisionReminderWorker.cancel(appContext, rev.id)
            repo.deleteRevision(rev)
        }
        repo.deleteSubject(subject) // cascades topics
    }

    // ── Session (Topic) CRUD ────────────────────────────────────────────────────

    /**
     * Creates or updates a session. When [editingTopicId] is null a new Topic is
     * inserted (find-or-creating its Subject) and a spaced-repetition revision is
     * scheduled. On edit, the Topic is replaced in place.
     */
    fun saveSession(form: SessionForm) = viewModelScope.launch {
        val discName = form.disc.trim()
        val topicTitle = form.topic.trim()
        if (discName.isEmpty() || topicTitle.isEmpty()) return@launch
        val duration = form.duration.toIntOrNull()?.coerceAtLeast(1) ?: 60

        val knownCount = uiState.value.subjects.size
        val existing = repo.getSubjectByName(discName, owner)
        val color = existing?.colorHex ?: nextColorFor(discName, knownCount)
        val subject = repo.findOrCreateSubject(discName, color, owner)

        val editId = form.editingTopicId
        if (editId == null) {
            val topicId = repo.insertTopic(
                Topic(
                    subjectId = subject.id,
                    title = topicTitle,
                    notes = form.notes.trim(),
                    durationMinutes = duration,
                    ownerEmail = owner,
                    studiedAt = form.dateMillis
                )
            ).toInt()
            scheduleRevisionFor(topicId, subject.id, topicTitle, subject.name, subject.colorHex, form.dateMillis)
        } else {
            repo.insertTopic(
                Topic(
                    id = editId,
                    subjectId = subject.id,
                    title = topicTitle,
                    notes = form.notes.trim(),
                    durationMinutes = duration,
                    ownerEmail = owner,
                    studiedAt = form.dateMillis
                )
            )
        }
    }

    fun deleteSession(topic: Topic) = viewModelScope.launch {
        repo.deleteTopic(topic)
    }

    fun deleteSession(session: TopicWithSubject) = deleteSession(session.topic)

    /** Schedules a spaced-repetition revision + WorkManager reminder. */
    private suspend fun scheduleRevisionFor(
        topicId: Int,
        subjectId: Int,
        topicTitle: String,
        subjectName: String,
        colorHex: String,
        studiedAt: Long
    ) {
        val cal = Calendar.getInstance().apply {
            timeInMillis = studiedAt
            add(Calendar.DAY_OF_YEAR, settingsStore.settings.value.revisionIntervalDays)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val scheduledAt = cal.timeInMillis
        val revision = Revision(
            topicId = topicId,
            subjectId = subjectId,
            topicTitle = topicTitle,
            subjectName = subjectName,
            subjectColorHex = colorHex,
            scheduledAt = scheduledAt,
            ownerEmail = owner
        )
        val id = repo.insertRevision(revision).toInt()
        val workerId = if (settingsStore.settings.value.notificationsEnabled) {
            RevisionReminderWorker.schedule(appContext, id, topicTitle, subjectName, scheduledAt).toString()
        } else ""
        repo.updateRevision(revision.copy(id = id, workerRequestId = workerId))
    }

    // ── Revisions ───────────────────────────────────────────────────────────────

    fun toggleRevisionDone(revision: Revision) = viewModelScope.launch {
        val newCompleted = !revision.isCompleted
        repo.setRevisionCompleted(revision.id, newCompleted)
        if (newCompleted && revision.workerRequestId.isNotBlank()) {
            RevisionReminderWorker.cancel(appContext, revision.id)
        }
    }

    /**
     * Manually creates or reschedules a revision. Picks/creates the discipline,
     * stores denormalized display fields, and (re)schedules the WorkManager reminder.
     */
    fun saveRevision(form: RevisionForm, onResult: (SaveResult) -> Unit = {}) = viewModelScope.launch {
        val subjName = form.subjectName.trim()
        val topicTitle = form.topicTitle.trim()
        if (subjName.isEmpty()) {
            onResult(SaveResult.Error("Disciplina é obrigatória.")); return@launch
        }
        if (topicTitle.isEmpty()) {
            onResult(SaveResult.Error("Tópico é obrigatório.")); return@launch
        }

        val knownCount = uiState.value.subjects.size
        val existing = repo.getSubjectByName(subjName, owner)
        val color = existing?.colorHex ?: nextColorFor(subjName, knownCount)
        val subject = repo.findOrCreateSubject(subjName, color, owner)

        val editId = form.editingRevisionId
        if (editId == null) {
            val base = Revision(
                topicId = form.topicId ?: 0,
                subjectId = subject.id,
                topicTitle = topicTitle,
                subjectName = subject.name,
                subjectColorHex = subject.colorHex,
                scheduledAt = form.scheduledAt,
                ownerEmail = owner
            )
            val id = repo.insertRevision(base).toInt()
            val workerId = if (settingsStore.settings.value.notificationsEnabled) {
                RevisionReminderWorker.schedule(appContext, id, topicTitle, subject.name, form.scheduledAt).toString()
            } else ""
            repo.updateRevision(base.copy(id = id, workerRequestId = workerId))
        } else {
            // Reschedule: cancel old reminder (by id tag), re-enqueue.
            RevisionReminderWorker.cancel(appContext, editId)
            val workerId = if (settingsStore.settings.value.notificationsEnabled) {
                RevisionReminderWorker.schedule(appContext, editId, topicTitle, subject.name, form.scheduledAt).toString()
            } else ""
            val existingRev = repo.getRevisionById(editId)
            repo.updateRevision(
                Revision(
                    id = editId,
                    topicId = existingRev?.topicId ?: form.topicId ?: 0,
                    subjectId = subject.id,
                    topicTitle = topicTitle,
                    subjectName = subject.name,
                    subjectColorHex = subject.colorHex,
                    scheduledAt = form.scheduledAt,
                    isCompleted = false,
                    ownerEmail = owner,
                    workerRequestId = workerId
                )
            )
        }
        onResult(SaveResult.Success)
    }

    fun deleteRevision(revision: Revision) = viewModelScope.launch {
        if (revision.workerRequestId.isNotBlank()) {
            RevisionReminderWorker.cancel(appContext, revision.id)
        }
        repo.deleteRevision(revision)
    }

    // ── Auth (local accounts, no backend) ─────────────────────────────────────────

    /**
     * Authenticates against a locally-registered account. Format validation happens
     * in the UI; this checks existence + password. Returns an error message, or null
     * on success (in which case the session is marked as signed in).
     */
    fun login(email: String, password: String): String? {
        val e = email.trim()
        if (!accountStore.exists(e)) return "Nenhuma conta encontrada com esse e-mail."
        if (!accountStore.authenticate(e, password)) return "Senha incorreta."
        settingsStore.login(e, accountStore.getName(e))
        return null
    }

    /**
     * Creates a new local account and signs in. Returns an error message, or null on
     * success.
     */
    fun register(name: String, email: String, password: String): String? {
        val e = email.trim()
        if (accountStore.exists(e)) return "Já existe uma conta com esse e-mail."
        accountStore.register(e, password, name.trim())
        settingsStore.login(e, name.trim())
        return null
    }

    /** Signs the user out and returns to the login gate. */
    fun logout() {
        settingsStore.logout()
    }

    // ── Settings (Perfil) ─────────────────────────────────────────────────────────

    /** Updates the spaced-repetition interval used when auto-scheduling new revisions. */
    fun setRevisionInterval(days: Int) {
        settingsStore.setRevisionIntervalDays(days)
    }

    /**
     * Enables/disables revision reminders. Disabling cancels all pending reminders;
     * enabling re-schedules reminders for pending, still-future revisions.
     */
    fun setNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        settingsStore.setNotificationsEnabled(enabled)
        val pending = uiState.value.revisions.filter { !it.isCompleted }
        if (enabled) {
            val now = System.currentTimeMillis()
            pending.filter { it.scheduledAt > now }.forEach { rev ->
                RevisionReminderWorker.cancel(appContext, rev.id) // avoid duplicate enqueue
                val workerId = RevisionReminderWorker.schedule(
                    appContext, rev.id, rev.topicTitle, rev.subjectName, rev.scheduledAt
                ).toString()
                repo.updateRevision(rev.copy(workerRequestId = workerId))
            }
        } else {
            pending.forEach { rev -> RevisionReminderWorker.cancel(appContext, rev.id) }
        }
    }

    fun weeklyMinutes(): Int {
        val since = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        return uiState.value.sessions.filter { it.topic.studiedAt >= since }
            .sumOf { it.topic.durationMinutes }
    }

    class Factory(
        private val repo: StudyRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            StudyViewModel(repo, context.applicationContext) as T
    }
}
