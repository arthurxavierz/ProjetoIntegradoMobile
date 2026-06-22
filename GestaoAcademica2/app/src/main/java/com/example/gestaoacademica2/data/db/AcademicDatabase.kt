package com.example.gestaoacademica2.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gestaoacademica2.data.model.AcademicEvent

@Database(entities = [AcademicEvent::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AcademicDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        @Volatile private var INSTANCE: AcademicDatabase? = null

        fun getInstance(context: Context): AcademicDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AcademicDatabase::class.java,
                    "academic_database"
                ).build().also { INSTANCE = it }
            }
    }
}
