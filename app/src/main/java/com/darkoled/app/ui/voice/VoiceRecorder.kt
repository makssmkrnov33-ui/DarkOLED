package com.darkoled.app.ui.voice

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

class VoiceRecorder {
    private val sampleRate = 44100
    private var isRecording = false

    suspend fun record(durationMs: Long = 5000): List<Int> = withContext(Dispatchers.IO) {
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        val recorder = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize)
        val amValues = mutableListOf<Int>()
        val buffer = ShortArray(bufferSize)

        isRecording = true
        recorder.startRecording()

        val totalSamples = (sampleRate * durationMs / 1000).toInt()
        var samplesRead = 0

        while (isRecording && samplesRead < totalSamples) {
            val read = recorder.read(buffer, 0, buffer.size)
            if (read > 0) {
                samplesRead += read
                var sum = 0.0
                for (i in 0 until read) sum += buffer[i].toDouble() * buffer[i].toDouble()
                val rms = sqrt(sum / read).toInt()
                amValues.add(rms)
            }
        }

        recorder.stop()
        recorder.release()
        amValues
    }

    fun stop() { isRecording = false }
}
