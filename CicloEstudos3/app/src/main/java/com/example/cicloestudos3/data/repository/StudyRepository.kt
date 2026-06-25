package com.example.cicloestudos3.data.repository

import com.example.cicloestudos3.data.db.StudyDatabase
import com.example.cicloestudos3.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class StudyRepository(private val db: StudyDatabase) {

    // ── Subjects ─────────────────────────────────────────────────────────────

    fun subjectsWithStats(owner: String): Flow<List<SubjectWithStats>> =
        db.subjectDao().getSubjectsWithStats(owner)

    fun allSubjects(owner: String): Flow<List<Subject>> =
        db.subjectDao().getAllSubjects(owner)

    suspend fun insertSubject(subject: Subject): Long =
        db.subjectDao().insert(subject)

    suspend fun updateSubject(subject: Subject) =
        db.subjectDao().update(subject)

    suspend fun deleteSubject(subject: Subject) =
        db.subjectDao().delete(subject)

    suspend fun getSubjectById(id: Int): Subject? =
        db.subjectDao().getById(id)

    suspend fun getSubjectByName(name: String, owner: String): Subject? =
        db.subjectDao().getByName(name, owner)

    suspend fun getSubjectByNameExcluding(name: String, excludeId: Int, owner: String): Subject? =
        db.subjectDao().getByNameExcluding(name, excludeId, owner)

    /** Returns an existing subject (per owner, case-insensitive) with this name or creates one. */
    suspend fun findOrCreateSubject(name: String, colorHex: String, owner: String): Subject {
        val existing = db.subjectDao().getByName(name.trim(), owner)
        if (existing != null) return existing
        val id = db.subjectDao().insert(Subject(name = name.trim(), colorHex = colorHex, ownerEmail = owner)).toInt()
        return db.subjectDao().getById(id) ?: Subject(id = id, name = name.trim(), colorHex = colorHex, ownerEmail = owner)
    }

    // ── Topics ────────────────────────────────────────────────────────────────

    fun getRecentTopics(owner: String, limit: Int = 20): Flow<List<TopicWithSubject>> =
        db.topicDao().getRecentTopicsWithSubject(owner, limit)

    fun getTopicsForSubject(subjectId: Int): Flow<List<TopicWithSubject>> =
        db.topicDao().getTopicsForSubject(subjectId)

    fun getAllTopics(owner: String): Flow<List<Topic>> =
        db.topicDao().getAllTopics(owner)

    fun allTopicsWithSubject(owner: String): Flow<List<TopicWithSubject>> =
        db.topicDao().getAllTopicsWithSubject(owner)

    suspend fun getTopicById(id: Int): Topic? =
        db.topicDao().getById(id)

    fun getTopicsSince(owner: String, epochMillis: Long): Flow<List<Topic>> =
        db.topicDao().getTopicsSince(owner, epochMillis)

    suspend fun insertTopic(topic: Topic): Long =
        db.topicDao().insert(topic)

    suspend fun deleteTopic(topic: Topic) =
        db.topicDao().delete(topic)

    // ── Revisions ─────────────────────────────────────────────────────────────

    fun allRevisions(owner: String): Flow<List<Revision>> =
        db.revisionDao().getAllRevisions(owner)

    fun getTodayRevisions(owner: String): Flow<List<Revision>> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.timeInMillis
        val endOfDay   = startOfDay + 86_400_000L
        return db.revisionDao().getTodayRevisions(owner, startOfDay, endOfDay)
    }

    fun getUpcomingRevisions(owner: String, limit: Int = 5): Flow<List<Revision>> =
        db.revisionDao().getUpcomingRevisions(owner, System.currentTimeMillis(), limit)

    suspend fun getRevisionById(id: Int): Revision? =
        db.revisionDao().getById(id)

    suspend fun getRevisionsBySubjectId(subjectId: Int): List<Revision> =
        db.revisionDao().getBySubjectId(subjectId)

    suspend fun updateRevisionSubjectInfo(subjectId: Int, name: String, colorHex: String) =
        db.revisionDao().updateSubjectInfo(subjectId, name, colorHex)

    suspend fun insertRevision(revision: Revision): Long =
        db.revisionDao().insert(revision)

    suspend fun updateRevision(revision: Revision) =
        db.revisionDao().update(revision)

    suspend fun deleteRevision(revision: Revision) =
        db.revisionDao().delete(revision)

    suspend fun setRevisionCompleted(id: Int, completed: Boolean) =
        db.revisionDao().setCompleted(id, completed)
}
