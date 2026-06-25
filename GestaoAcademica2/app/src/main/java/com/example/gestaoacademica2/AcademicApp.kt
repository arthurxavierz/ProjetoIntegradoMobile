package com.example.gestaoacademica2

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.gestaoacademica2.data.db.AcademicDatabase
import com.example.gestaoacademica2.data.repository.EventRepository

class AcademicApp : Application() {

    val database: AcademicDatabase by lazy { AcademicDatabase.getInstance(this) }
    val repository: EventRepository by lazy { EventRepository(database.eventDao()) }

    override fun onCreate() {
        super.onCreate()
        // O protótipo é light-only.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
