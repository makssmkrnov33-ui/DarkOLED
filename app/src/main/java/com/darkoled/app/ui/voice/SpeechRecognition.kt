package com.darkoled.app.ui.voice

import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

fun createSpeechRecognizer(): SpeechRecognizer? {
    return null
}

fun createSpeechIntent(): Intent {
    return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    }
}
