package com.example.cicloestudos3.data.db

import androidx.room.*
import com.example.cicloestudos3.data.model.Revision
import kotlinx.coroutines.flow.Flow

@Dao
interface RevisionDao {

    @Query("SELECT * FROM revisions WHERE ownerEmail = :owner ORDER BY scheduledAt ASC")
    fun getAllRevisions(owner: String): Flow<List<Revision>>

    @Query("""
        SELECT * FROM revisions
        WHERE ownerEmail = :owner AND scheduledAt >= :startOfDay AND scheduledAt < :endOfDay
        ORDER BY scheduledAt ASC
    """)
    fun getTodayRevisions(owner: String, startOfDay: Long, endOfDay: Long): Flow<List<Revision>>

    @Query("""
        SELECT * FROM revisions
        WHERE ownerEmail = :owner AND scheduledAt >= :now AND isCompleted = 0
        ORDER BY scheduledAt ASC
        LIMIT :limit
    """)
    fun getUpcomingRevisions(owner: String, now: Long, limit: Int = 5): Flow<List<Revision>>

    @Query("SELECT * FROM revisions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Revision?

    @Query("SELECT * FROM revisions WHERE subjectId = :subjectId")
    suspend fun getBySubjectId(subjectId: Int): List<Revision>

    /** Keep denormalized name/color in sync after a Subject is renamed/recolored. */
    @Query("UPDATE revisions SET subjectName = :name, subjectColorHex = :colorHex WHERE subjectId = :subjectId")
    suspend fun updateSubjectInfo(subjectId: Int, name: String, colorHex: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(revision: Revision): Long

    @Update
    suspend fun update(revision: Revision)

    @Delete
    suspend fun delete(revision: Revision)

    @Query("UPDATE revisions SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Int, completed: Boolean)
}
