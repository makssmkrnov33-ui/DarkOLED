package com.darkoled.app.engine

import android.app.Application

object MessengerManager {
    lateinit var engine: MessengerEngine
        private set
    lateinit var voiceAI: VoiceAIEngine
        private set
    lateinit var securityAI: SecurityAI
        private set

    fun init(app: Application) {
        if (!::engine.isInitialized) {
            engine = MessengerEngine(app)
        }
        if (!::voiceAI.isInitialized) {
            voiceAI = VoiceAIEngine(app)
            voiceAI.init()
        }
        if (!::securityAI.isInitialized) {
            securityAI = SecurityAI(app)
        }
    }

    fun destroy() {
        voiceAI.destroy()
    }
}
