package com.example.cicloestudos3.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "topics",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Topic(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int,
    val title: String,
    val notes: String = "",
    val durationMinutes: Int,
    val ownerEmail: String = "",
    val studiedAt: Long = System.currentTimeMillis()
)

/** Flat join used by DAO queries — no @DatabaseView needed */
data class TopicWithSubject(
    @Embedded val topic: Topic,
    @ColumnInfo(name = "subjectName")     val subjectName: String,
    @ColumnInfo(name = "subjectColorHex") val subjectColorHex: String
)

/** Subject aggregate used by home screen */
data class SubjectWithStats(
    @Embedded val subject: Subject,
    @ColumnInfo(name = "topicCount")    val topicCount: Int,
    @ColumnInfo(name = "totalMinutes")  val totalMinutes: Int
)
