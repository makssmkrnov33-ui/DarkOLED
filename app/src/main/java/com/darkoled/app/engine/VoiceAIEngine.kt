package com.darkoled.app.engine

import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceAIEngine(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var recognizer: SpeechRecognizer? = null
    private var ttsReady = false
    private var listening = false

    private val _isListening = MutableStateFlow(false)
    val isListeningFlow: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isLearning = MutableStateFlow(false)
    val isLearning: StateFlow<Boolean> = _isLearning.asStateFlow()

    private val _transcript = MutableStateFlow("")
    val transcript: StateFlow<String> = _transcript.asStateFlow()

    private val _status = MutableStateFlow("Загрузка голоса...")
    val status: StateFlow<String> = _status.asStateFlow()

    private val memory = mutableMapOf<String, String>()
    private val learned = mutableListOf<String>()

    private val responses = mapOf(
        "привет" to "Приветик! Я твоя ассистентка. Как настроение?",
        "как дела" to "У меня всё замечательно! Спасибо, что спросил!",
        "пока" to "Пока-пока! Буду ждать нашего следующего разговора!",
        "спасибо" to "Пожалуйста! Я всегда рада помочь!",
        "помощь" to "Я запоминаю всё, что ты говоришь в режиме обучения. Просто нажми 🎓!",
        "кто тебя создал" to "Меня создала команда DarkOLED. Они вложили в меня всю свою любовь!",
        "расскажи о себе" to "Мне 18 лет, я голосовая ассистентка. Я учусь каждый день, чтобы лучше понимать тебя!",
        "ты красивая" to "Ой, спасибо! Ты меня смущаешь 🥰",
        "что ты умеешь" to "Я слушаю, запоминаю и отвечаю голосом. Включи обучение — и я запомню всё, что ты скажешь!"
    )

    fun init() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ru")
                tts?.setPitch(1.3f)
                tts?.setSpeechRate(0.9f)
                try {
                    val voice = tts?.voices?.find {
                        it.name.contains("female", true) || it.name.contains("ru-ru", true)
                    }
                    voice?.let { tts?.voice = it }
                } catch (_: Exception) {}
                ttsReady = true
                _status.value = "Голос загружен"
                if (listening) startRecognizer()
            }
        }
    }

    fun speak(text: String) {
        if (!ttsReady) return
        _isSpeaking.value = true
        _status.value = "Говорю..."
        tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(uttId: String?) {}
            override fun onDone(uttId: String?) {
                _isSpeaking.value = false
                _status.value = "Слушаю..."
            }
            override fun onError(uttId: String?) {
                _isSpeaking.value = false
                _status.value = "Слушаю..."
            }
        })
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ai")
    }

    fun toggleLearning() {
        val new = !_isLearning.value
        _isLearning.value = new
        _status.value = if (new) "🎓 Учусь! Говори что-нибудь" else "Слушаю..."
    }

    private fun learn(text: String) {
        if (_isLearning.value && text.isNotBlank()) {
            learned.add(text)
            memory[text.lowercase().take(50)] = "Я запомнила: $text"
            _status.value = "📝 Запомнила: ${text.take(30)}..."
        }
    }

    private fun respond(input: String): String {
        val lower = input.lowercase().trim()
        for (item in learned.reversed()) {
            if (lower.contains(item.lowercase().take(20))) {
                return "Я помню! Ты говорил: $item"
            }
        }
        return memory.entries.find { lower.contains(it.key) }?.value
            ?: responses.entries.find { lower.contains(it.key) }?.value
            ?: "Ой, я не совсем поняла... Расскажи подробнее, я запомню!"
    }

    private fun startRecognizer() {
        stopRecognizer()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { _status.value = "🎤 Слушаю..." }
            override fun onBeginningOfSpeech() { _status.value = "🎤 Слышу тебя..." }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { _status.value = "Обрабатываю..." }
            override fun onError(error: Int) {
                if (listening) scheduleRestart()
            }
            override fun onResults(results: Bundle?) {
                val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (text != null) {
                    _transcript.value = text
                    learn(text)
                    if (!_isLearning.value) {
                        val reply = respond(text)
                        speak(reply)
                    }
                }
                if (listening) scheduleRestart()
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        doListen()
    }

    private fun doListen() {
        try {
            val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ru-RU")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            recognizer?.startListening(intent)
        } catch (_: Exception) {
            _status.value = "Ошибка микрофона"
        }
    }

    private fun stopRecognizer() {
        try {
            recognizer?.stopListening()
            recognizer?.destroy()
        } catch (_: Exception) {}
        recognizer = null
    }

    fun startContinuousListening() {
        listening = true
        _isListening.value = true
        if (ttsReady) {
            startRecognizer()
        } else {
            _status.value = "Загрузка голоса..."
        }
    }

    fun stopContinuousListening() {
        listening = false
        _isListening.value = false
        stopRecognizer()
        tts?.stop()
        _status.value = "Остановлена"
    }

    private fun scheduleRestart() {
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (listening) doListen()
        }, 300)
    }

    fun destroy() {
        stopContinuousListening()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }
}
