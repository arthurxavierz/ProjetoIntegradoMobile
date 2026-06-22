package com.example.gestaoacademica2

import android.app.Application
import com.example.gestaoacademica2.data.db.AcademicDatabase
import com.example.gestaoacademica2.data.repository.EventRepository

class AcademicApp : Application() {
    val database: AcademicDatabase by lazy { AcademicDatabase.getInstance(this) }
    val repository: EventRepository by lazy { EventRepository(database.eventDao()) }
}
