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
        WHERE s.ownerEmail = :owner
        GROUP BY s.id
        ORDER BY s.name ASC
    """)
    fun getSubjectsWithStats(owner: String): Flow<List<SubjectWithStats>>

    @Query("SELECT * FROM subjects WHERE ownerEmail = :owner ORDER BY name ASC")
    fun getAllSubjects(owner: String): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Subject?

    @Query("SELECT * FROM subjects WHERE name = :name COLLATE NOCASE AND ownerEmail = :owner LIMIT 1")
    suspend fun getByName(name: String, owner: String): Subject?

    @Query("SELECT * FROM subjects WHERE name = :name COLLATE NOCASE AND ownerEmail = :owner AND id <> :excludeId LIMIT 1")
    suspend fun getByNameExcluding(name: String, excludeId: Int, owner: String): Subject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subject: Subject): Long

    @Update
    suspend fun update(subject: Subject)

    @Delete
    suspend fun delete(subject: Subject)
}
