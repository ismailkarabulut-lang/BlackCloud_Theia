// === app/src/main/java/com/blackcloud/shell/voice/VoiceInputManager.kt ===
package com.blackcloud.shell.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

/**
 * Ses tanımayla ilgili asistan durum/sonuç tipleri.
 */
sealed class VoiceResult {
    object Idle : VoiceResult()
    object Ready : VoiceResult()
    object Listening : VoiceResult()
    data class Partial(val text: String) : VoiceResult()
    data class Success(val text: String) : VoiceResult()
    data class Error(val error: String) : VoiceResult()
}

/**
 * Android yerel SpeechRecognizer API'sini sarmalayan yöneticidir.
 * Mikrofon girdisini Türkçe locale (tr-TR) kullanarak metne (STT) dönüştürür.
 */
class VoiceInputManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    /**
     * Kullanıcı konuşurken mikrofon girdisini dinler ve durumu bir Flow olarak döndürür.
     */
    fun startListening(): Flow<VoiceResult> = callbackFlow {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            trySend(VoiceResult.Error("Cihazda ses tanıma özelliği mevcut değil."))
            close()
            return@callbackFlow
        }

        var recognizer: SpeechRecognizer? = null
        try {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext)
            speechRecognizer = recognizer
        } catch (e: Exception) {
            trySend(VoiceResult.Error("Ses tanıma servis başlatma hatası: ${e.localizedMessage}"))
            close()
            return@callbackFlow
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                trySend(VoiceResult.Ready)
            }

            override fun onBeginningOfSpeech() {
                trySend(VoiceResult.Listening)
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Mikrofon ses hatası."
                    SpeechRecognizer.ERROR_CLIENT -> "İstemci hatası."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Mikrofon izni eksik."
                    SpeechRecognizer.ERROR_NETWORK -> "Ağ bağlantı hatası."
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Ağ zaman aşımı."
                    SpeechRecognizer.ERROR_NO_MATCH -> "Ses anlaşılamadı, lütfen tekrar deneyin."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Ses servisi meşgul."
                    SpeechRecognizer.ERROR_SERVER -> "Sunucu hatası."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Ses girişi zaman aşımına uğradı."
                    else -> "Bilinmeyen ses tanıma hatası."
                }
                trySend(VoiceResult.Error(message))
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    trySend(VoiceResult.Success(matches[0]))
                } else {
                    trySend(VoiceResult.Error("Sesli yanıt alınamadı."))
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    trySend(VoiceResult.Partial(matches[0]))
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        try {
            recognizer.setRecognitionListener(listener)
            recognizer.startListening(intent)
        } catch (e: Exception) {
            trySend(VoiceResult.Error("Dinleme başlatma hatası: ${e.localizedMessage}"))
            close()
        }

        awaitClose {
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                try {
                    recognizer.stopListening()
                    recognizer.destroy()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            speechRecognizer = null
        }
    }

    /**
     * Dinlemeyi elle durdurur.
     */
    fun stopListening() {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
