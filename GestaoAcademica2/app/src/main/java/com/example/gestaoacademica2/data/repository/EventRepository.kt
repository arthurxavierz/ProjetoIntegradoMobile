package com.example.gestaoacademica2.data.repository

import com.example.gestaoacademica2.data.db.EventDao
import com.example.gestaoacademica2.data.model.AcademicEvent
import kotlinx.coroutines.flow.Flow

class EventRepository(private val dao: EventDao) {

    val allEvents: Flow<List<AcademicEvent>> = dao.getAllEvents()

    suspend fun upsert(event: AcademicEvent)                   = dao.upsert(event)
    suspend fun delete(event: AcademicEvent)                   = dao.delete(event)
    suspend fun getById(id: Int): AcademicEvent?               = dao.getById(id)
    suspend fun setFavorite(id: Int, isFavorite: Boolean)      = dao.setFavorite(id, isFavorite)
    suspend fun setCompleted(id: Int, isCompleted: Boolean)    = dao.setCompleted(id, isCompleted)
}
