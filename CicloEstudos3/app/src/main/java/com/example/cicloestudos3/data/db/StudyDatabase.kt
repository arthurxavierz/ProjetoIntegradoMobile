package com.example.cicloestudos3.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cicloestudos3.data.model.Revision
import com.example.cicloestudos3.data.model.Subject
import com.example.cicloestudos3.data.model.Topic

@Database(
    entities = [Subject::class, Topic::class, Revision::class],
    version = 1,
    exportSchema = false
)
abstract class StudyDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun topicDao(): TopicDao
    abstract fun revisionDao(): RevisionDao

    companion object {
        @Volatile private var INSTANCE: StudyDatabase? = null

        fun getInstance(context: Context): StudyDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StudyDatabase::class.java,
                    "ciclo_estudos.db"
                ).build().also { INSTANCE = it }
            }
    }
}
