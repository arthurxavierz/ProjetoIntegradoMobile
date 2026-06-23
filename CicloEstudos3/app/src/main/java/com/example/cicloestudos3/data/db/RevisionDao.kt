package com.example.cicloestudos3.data.db

import androidx.room.*
import com.example.cicloestudos3.data.model.Revision
import kotlinx.coroutines.flow.Flow

@Dao
interface RevisionDao {

    @Query("SELECT * FROM revisions ORDER BY scheduledAt ASC")
    fun getAllRevisions(): Flow<List<Revision>>

    @Query("""
        SELECT * FROM revisions
        WHERE scheduledAt >= :startOfDay AND scheduledAt < :endOfDay
        ORDER BY scheduledAt ASC
    """)
    fun getTodayRevisions(startOfDay: Long, endOfDay: Long): Flow<List<Revision>>

    @Query("""
        SELECT * FROM revisions
        WHERE scheduledAt >= :now AND isCompleted = 0
        ORDER BY scheduledAt ASC
        LIMIT :limit
    """)
    fun getUpcomingRevisions(now: Long, limit: Int = 5): Flow<List<Revision>>

    @Query("SELECT * FROM revisions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Revision?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(revision: Revision): Long

    @Update
    suspend fun update(revision: Revision)

    @Delete
    suspend fun delete(revision: Revision)

    @Query("UPDATE revisions SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Int, completed: Boolean)
}
