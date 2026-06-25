package com.example.gestaoacademica2.data.repository

import com.example.gestaoacademica2.data.db.EventDao
import com.example.gestaoacademica2.data.model.AcademicEvent
import kotlinx.coroutines.flow.Flow

class EventRepository(private val dao: EventDao) {

    /** Fluxo dos eventos de um usuário específico. */
    fun events(owner: String): Flow<List<AcademicEvent>> = dao.getAllEvents(owner)

    suspend fun insert(event: AcademicEvent): Long          = dao.insert(event)
    suspend fun update(event: AcademicEvent)                = dao.update(event)
    suspend fun delete(event: AcademicEvent)                = dao.delete(event)
    suspend fun deleteById(id: Long)                        = dao.deleteById(id)
    suspend fun getById(id: Long): AcademicEvent?           = dao.getById(id)
    suspend fun setFavorite(id: Long, isFavorite: Boolean)  = dao.setFavorite(id, isFavorite)
}
