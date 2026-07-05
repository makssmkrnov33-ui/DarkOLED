class VoiceRecorder {
    private var recorder: AudioRecord? = null
    private val waveform = mutableListOf<Int>()
    
    fun startRecording() {
        val bufferSize = AudioRecord.getMinBufferSize(
            44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
        )
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC, 44100,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize
        )
        recorder?.startRecording()
        
        // В отдельном корутине читаем буфер и считаем RMS
        CoroutineScope(Dispatchers.IO).launch {
            val buffer = ShortArray(bufferSize / 2)
            while (recorder?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                recorder?.read(buffer, 0, buffer.size)
                val rms = sqrt(buffer.map { it * it }.average()).toInt()
                waveform.add(rms)
            }
        }
    }
    
    fun stopRecording(): List<Int> {
        recorder?.stop()
        recorder?.release()
        return waveform.toList()
    }
}