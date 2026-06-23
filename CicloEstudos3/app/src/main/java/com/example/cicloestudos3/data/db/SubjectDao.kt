package com.example.cicloestudos3.data.db

import androidx.room.*
import com.example.cicloestudos3.data.model.Subject
import com.example.cicloestudos3.data.model.SubjectWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Query("""
        SELECT s.*,
               COUNT(t.id)                          AS topicCount,
               COALESCE(SUM(t.durationMinutes), 0)  AS totalMinutes
        FROM subjects s
        LEFT JOIN topics t ON t.subjectId = s.id
        GROUP BY s.id
        ORDER BY s.name ASC
    """)
    fun getSubjectsWithStats(): Flow<List<SubjectWithStats>>

    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Subject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subject: Subject): Long

    @Delete
    suspend fun delete(subject: Subject)
}
