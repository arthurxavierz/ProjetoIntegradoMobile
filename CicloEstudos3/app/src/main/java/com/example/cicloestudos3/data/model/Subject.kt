package com.example.cicloestudos3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String,   // e.g. "#22C55E"
    val createdAt: Long = System.currentTimeMillis()
)

/** Preset palette for subject color picker */
val subjectColorPalette = listOf(
    "#EF4444", // Red
    "#F97316", // Orange
    "#F59E0B", // Amber
    "#22C55E", // Green
    "#14B8A6", // Teal
    "#3B82F6", // Blue
    "#8B5CF6", // Purple
    "#EC4899", // Pink
    "#10B981", // Emerald
    "#64748B", // Slate
)
