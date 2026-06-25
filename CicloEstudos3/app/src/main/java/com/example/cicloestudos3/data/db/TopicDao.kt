package com.example.cicloestudos3.data.db

import androidx.room.*
import com.example.cicloestudos3.data.model.Topic
import com.example.cicloestudos3.data.model.TopicWithSubject
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Query("""
        SELECT t.*, s.name AS subjectName, s.colorHex AS subjectColorHex
        FROM topics t
        INNER JOIN subjects s ON s.id = t.subjectId
        WHERE s.ownerEmail = :owner
        ORDER BY t.studiedAt DESC
        LIMIT :limit
    """)
    fun getRecentTopicsWithSubject(owner: String, limit: Int = 20): Flow<List<TopicWithSubject>>

    @Query("""
        SELECT t.*, s.name AS subjectName, s.colorHex AS subjectColorHex
        FROM topics t
        INNER JOIN subjects s ON s.id = t.subjectId
        WHERE t.subjectId = :subjectId
        ORDER BY t.studiedAt DESC
    """)
    fun getTopicsForSubject(subjectId: Int): Flow<List<TopicWithSubject>>

    @Query("""
        SELECT t.*, s.name AS subjectName, s.colorHex AS subjectColorHex
        FROM topics t
        INNER JOIN subjects s ON s.id = t.subjectId
        WHERE s.ownerEmail = :owner
        ORDER BY t.studiedAt DESC
    """)
    fun getAllTopicsWithSubject(owner: String): Flow<List<TopicWithSubject>>

    @Query("SELECT * FROM topics WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Topic?

    @Query("SELECT * FROM topics WHERE ownerEmail = :owner ORDER BY studiedAt DESC")
    fun getAllTopics(owner: String): Flow<List<Topic>>

    @Query("SELECT * FROM topics WHERE ownerEmail = :owner AND studiedAt >= :since ORDER BY studiedAt DESC")
    fun getTopicsSince(owner: String, since: Long): Flow<List<Topic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(topic: Topic): Long

    @Delete
    suspend fun delete(topic: Topic)
}
