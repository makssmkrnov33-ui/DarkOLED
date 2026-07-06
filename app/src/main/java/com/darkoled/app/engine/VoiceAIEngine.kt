package com.darkoled.app.engine

import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceAIEngine(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var recognizer: SpeechRecognizer? = null
    private var isListening = false

    private val _isListening = MutableStateFlow(false)
    val isListeningFlow: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isLearning = MutableStateFlow(false)
    val isLearning: StateFlow<Boolean> = _isLearning.asStateFlow()

    private val _transcript = MutableStateFlow("")
    val transcript: StateFlow<String> = _transcript.asStateFlow()

    private val _status = MutableStateFlow("Готова")
    val status: StateFlow<String> = _status.asStateFlow()

    private val memory = mutableMapOf<String, String>()
    private val learned = mutableListOf<String>()

    private val defaultResponses = mapOf(
        "привет" to "Приветик! Я твоя голосовая ассистентка. Как дела?",
        "как дела" to "У меня всё отлично! Спасибо, что спросил!",
        "пока" to "Пока-пока! Буду скучать!",
        "спасибо" to "Пожалуйста! Обращайся ещё!",
        "помощь" to "Я могу запоминать всё, что ты говоришь. Просто нажми кнопку обучения и скажи что-нибудь!",
        "расскажи о себе" to "Я молодая ассистентка, мне 18 лет. Я учусь на каждом разговоре и становлюсь умнее!",
        "кто ты" to "Я твой голосовой помощник с красивым молодым голосом. Я запоминаю всё, что ты говоришь в режиме обучения.",
        "что ты умеешь" to "Я слушаю, запоминаю и отвечаю голосом. Нажми 🎓 обучение, и я запомню всё, что ты скажешь!"
    )

    fun init() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ru")
                tts?.setPitch(1.3f)
                tts?.setSpeechRate(0.9f)
                try {
                    val voices = tts?.voices?.filter { it.name.contains("female", true) || it.name.contains("ru", true) }
                    voices?.firstOrNull()?.let { tts?.voice = it }
                } catch (_: Exception) {}
                _status.value = "Голос загружен"
                startListening()
            }
        }
    }

    fun speak(text: String) {
        _isSpeaking.value = true
        _status.value = "Говорю..."
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ai_msg")
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
        _status.value = "Слушаю..."
    }

    fun toggleLearning() {
        val new = !_isLearning.value
        _isLearning.value = new
        _status.value = if (new) "🎓 Учусь! Говори что-нибудь" else "Слушаю..."
    }

    fun learn(text: String) {
        if (_isLearning.value && text.isNotBlank()) {
            learned.add(text)
            memory[text.lowercase().take(50)] = "Я запомнила: $text"
            _status.value = "📝 Запомнила! ($text)"
        }
    }

    fun respond(input: String): String {
        val lower = input.lowercase().trim()

        if (learned.isNotEmpty()) {
            for (item in learned) {
                if (lower.contains(item.lowercase().take(20))) {
                    return "Я помню! Ты говорил: $item"
                }
            }
        }

        return memory.entries.find { lower.contains(it.key) }?.value
            ?: defaultResponses.entries.find { lower.contains(it.key) }?.value
            ?: "Ой, я не совсем поняла... Расскажи подробнее, я запомню! 🎀"
    }

    private fun startListening() {
        if (recognizer != null) return
        recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { _status.value = "🎤 Слушаю..." }
            override fun onBeginningOfSpeech() { _status.value = "🎤 Слышу..." }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { _status.value = "Обрабатываю..." }
            override fun onError(error: Int) {
                _status.value = "Ошибка $error"
                if (isListening) scheduleRestart()
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
                if (isListening) scheduleRestart()
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startContinuousListening() {
        isListening = true
        _isListening.value = true
        restartListening()
    }

    fun stopContinuousListening() {
        isListening = false
        _isListening.value = false
        recognizer?.stopListening()
        tts?.stop()
        _status.value = "Остановлена"
    }

    private fun scheduleRestart() {
        if (isListening) {
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                if (isListening) restartListening()
            }, 500)
        }
    }

    private fun restartListening() {
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
            _status.value = "Ошибка запуска"
        }
    }

    fun destroy() {
        stopContinuousListening()
        recognizer?.destroy()
        tts?.stop()
        tts?.shutdown()
    }
}
