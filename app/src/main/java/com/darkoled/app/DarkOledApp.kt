package com.darkoled.app

import android.app.Application
import com.darkoled.app.data.local.AppDatabase

class DarkOledApp : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
    }
}
