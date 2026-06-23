package com.example.cicloestudos3

import android.app.Application
import com.example.cicloestudos3.data.db.StudyDatabase
import com.example.cicloestudos3.data.repository.StudyRepository
import com.example.cicloestudos3.notification.NotificationHelper

class CicloApp : Application() {
    val database: StudyDatabase by lazy { StudyDatabase.getInstance(this) }
    val repository: StudyRepository by lazy { StudyRepository(database) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}
