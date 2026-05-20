// === app/src/main/java/com/blackcloud/shell/voice/VoiceOutputManager.kt ===
package com.blackcloud.shell.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Android yerel TextToSpeech (TTS) API'sini sarmalayan yöneticidir.
 * Asistan yanıtlarını veya seçilen sohbet mesajlarını Türkçe dil seçeneğiyle sesli okur.
 */
class VoiceOutputManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val turkishLocale = Locale("tr", "TR")

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(turkishLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("VoiceOutputManager", "Türkçe dil desteği cihazda yüklü değil, standart yerel dil seçiliyor.")
                tts?.setLanguage(Locale.getDefault())
            }
            isInitialized = true
        } else {
            Log.e("VoiceOutputManager", "TextToSpeech başlatılamadı.")
        }
    }

    /**
     * Verilen metni sesli olarak okur.
     */
    fun speak(text: String) {
        if (isInitialized && !text.isBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TheiaTalkId")
        }
    }

    /**
     * Konuşmayı yarıda keser ve susturur.
     */
    fun stop() {
        if (isInitialized) {
            tts?.stop()
        }
    }

    /**
     * Kaynakları serbest bırakır.
     */
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
