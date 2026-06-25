package com.example.cicloestudos3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "revisions")
data class Revision(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topicId: Int,
    val subjectId: Int,
    /** Denormalized for quick display without joins */
    val topicTitle: String,
    val subjectName: String,
    val subjectColorHex: String,
    /** Epoch millis for scheduled date+time */
    val scheduledAt: Long,
    val isCompleted: Boolean = false,
    val ownerEmail: String = "",
    /** UUID string of the WorkManager OneTimeWorkRequest */
    val workerRequestId: String = ""
)
