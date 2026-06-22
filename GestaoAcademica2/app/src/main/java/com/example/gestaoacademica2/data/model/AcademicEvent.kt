package com.example.gestaoacademica2.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class AcademicEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String = "",
    val description: String = "",
    val location: String = "",
    val category: EventCategory,
    val priority: Priority = Priority.MEDIA,
    val date: Long,                          // timestamp em ms
    val isFavorite: Boolean = false,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
