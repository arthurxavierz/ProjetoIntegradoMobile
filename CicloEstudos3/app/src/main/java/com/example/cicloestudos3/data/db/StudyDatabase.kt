package com.example.cicloestudos3.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cicloestudos3.data.model.Revision
import com.example.cicloestudos3.data.model.Subject
import com.example.cicloestudos3.data.model.Topic

@Database(
    entities = [Subject::class, Topic::class, Revision::class],
    version = 2,
    exportSchema = false
)
abstract class StudyDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun topicDao(): TopicDao
    abstract fun revisionDao(): RevisionDao

    companion object {
        @Volatile private var INSTANCE: StudyDatabase? = null

        /** v1 → v2: add per-user ownership column to every table. */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE subjects ADD COLUMN ownerEmail TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE topics ADD COLUMN ownerEmail TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE revisions ADD COLUMN ownerEmail TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getInstance(context: Context): StudyDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StudyDatabase::class.java,
                    "ciclo_estudos.db"
                ).addMigrations(MIGRATION_1_2).build().also { INSTANCE = it }
            }
    }
}
