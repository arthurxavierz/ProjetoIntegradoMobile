package com.example.gestaoacademica2.data.db

import androidx.room.*
import com.example.gestaoacademica2.data.model.AcademicEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    /** Eventos do usuário (escopo por e-mail) — cada conta vê só os seus. */
    @Query("SELECT * FROM events WHERE ownerEmail = :owner ORDER BY date ASC, time ASC")
    fun getAllEvents(owner: String): Flow<List<AcademicEvent>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): AcademicEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: AcademicEvent): Long

    @Update
    suspend fun update(event: AcademicEvent)

    @Delete
    suspend fun delete(event: AcademicEvent)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE events SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)
}
