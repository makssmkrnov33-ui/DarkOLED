package com.darkoled.app.engine

import android.app.Application

object MessengerManager {
    lateinit var engine: MessengerEngine
        private set

    fun init(app: Application) {
        if (!::engine.isInitialized) {
            engine = MessengerEngine(app)
        }
    }
}
