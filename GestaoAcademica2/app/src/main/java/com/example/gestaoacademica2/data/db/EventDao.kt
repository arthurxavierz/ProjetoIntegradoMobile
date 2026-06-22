package com.example.gestaoacademica2.data.db

import androidx.room.*
import com.example.gestaoacademica2.data.model.AcademicEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<AcademicEvent>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): AcademicEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(event: AcademicEvent)

    @Delete
    suspend fun delete(event: AcademicEvent)

    @Query("UPDATE events SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Int, isFavorite: Boolean)

    @Query("UPDATE events SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun setCompleted(id: Int, isCompleted: Boolean)
}
