package com.example.gestaoacademica2.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Evento acadêmico. As datas/horas são guardadas como texto (yyyy-MM-dd / HH:mm),
 * espelhando o estado do protótipo, o que facilita comparação e ordenação lexical.
 */
@Entity(tableName = "events")
data class AcademicEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: EventType,
    val date: String,            // yyyy-MM-dd
    val time: String,            // HH:mm
    val location: String = "",
    val isFavorite: Boolean = false,
    val ownerEmail: String = ""  // e-mail do dono; cada conta tem seus próprios eventos
)
