package com.example.cicloestudos3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String,   // e.g. "#22C55E"
    val ownerEmail: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
