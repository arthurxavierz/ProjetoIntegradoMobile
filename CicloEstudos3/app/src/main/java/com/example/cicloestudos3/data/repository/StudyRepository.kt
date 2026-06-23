package com.example.cicloestudos3.data.repository

import com.example.cicloestudos3.data.db.StudyDatabase
import com.example.cicloestudos3.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class StudyRepository(private val db: StudyDatabase) {

    // ── Subjects ─────────────────────────────────────────────────────────────

    val subjectsWithStats: Flow<List<SubjectWithStats>> =
        db.subjectDao().getSubjectsWithStats()

    val allSubjects: Flow<List<Subject>> =
        db.subjectDao().getAllSubjects()

    suspend fun insertSubject(subject: Subject): Long =
        db.subjectDao().insert(subject)

    suspend fun deleteSubject(subject: Subject) =
        db.subjectDao().delete(subject)

    suspend fun getSubjectById(id: Int): Subject? =
        db.subjectDao().getById(id)

    // ── Topics ────────────────────────────────────────────────────────────────

    fun getRecentTopics(limit: Int = 20): Flow<List<TopicWithSubject>> =
        db.topicDao().getRecentTopicsWithSubject(limit)

    fun getTopicsForSubject(subjectId: Int): Flow<List<TopicWithSubject>> =
        db.topicDao().getTopicsForSubject(subjectId)

    fun getAllTopics(): Flow<List<Topic>> =
        db.topicDao().getAllTopics()

    fun getTopicsSince(epochMillis: Long): Flow<List<Topic>> =
        db.topicDao().getTopicsSince(epochMillis)

    suspend fun insertTopic(topic: Topic): Long =
        db.topicDao().insert(topic)

    suspend fun deleteTopic(topic: Topic) =
        db.topicDao().delete(topic)

    // ── Revisions ─────────────────────────────────────────────────────────────

    val allRevisions: Flow<List<Revision>> =
        db.revisionDao().getAllRevisions()

    fun getTodayRevisions(): Flow<List<Revision>> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.timeInMillis
        val endOfDay   = startOfDay + 86_400_000L
        return db.revisionDao().getTodayRevisions(startOfDay, endOfDay)
    }

    fun getUpcomingRevisions(limit: Int = 5): Flow<List<Revision>> =
        db.revisionDao().getUpcomingRevisions(System.currentTimeMillis(), limit)

    suspend fun getRevisionById(id: Int): Revision? =
        db.revisionDao().getById(id)

    suspend fun insertRevision(revision: Revision): Long =
        db.revisionDao().insert(revision)

    suspend fun updateRevision(revision: Revision) =
        db.revisionDao().update(revision)

    suspend fun deleteRevision(revision: Revision) =
        db.revisionDao().delete(revision)

    suspend fun setRevisionCompleted(id: Int, completed: Boolean) =
        db.revisionDao().setCompleted(id, completed)
}
