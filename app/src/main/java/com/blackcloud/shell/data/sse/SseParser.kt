// === app/src/main/java/com/blackcloud/shell/data/sse/SseParser.kt ===
package com.blackcloud.shell.data.sse

import com.blackcloud.shell.data.model.KkypMetadata
import com.blackcloud.shell.data.model.SseEvent
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * SSE (Server-Sent Events) protokolü üzerinden akan ham verileri ayrıştırır.
 * "event:" ve "data:" belirteçlerini okuyarak bunları SseEvent nesnelerine dönüştürür.
 */
class SseParser {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val mapType = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
    private val mapAdapter = moshi.adapter<Map<String, Any>>(mapType)
    private val kkypAdapter = moshi.adapter(KkypMetadata::class.java)

    private var currentEvent: String? = null

    /**
     * Gelen tek bir satırı işler. Bir SseEvent döndürebilir veya ara durumdaysa null döner.
     */
    fun parseLine(line: String): SseEvent? {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) return null

        return when {
            trimmed.startsWith("event:") -> {
                currentEvent = trimmed.removePrefix("event:").trim()
                null
            }
            trimmed.startsWith("data:") -> {
                val dataJson = trimmed.removePrefix("data:").trim()
                val event = currentEvent ?: "text" // Varsayılan olarak text alalım
                val result = try {
                    parseData(event, dataJson)
                } catch (e: Exception) {
                    SseEvent.Error("Veri ayrıştırma hatası: ${e.message}")
                }
                currentEvent = null // Her data satırından sonra temizle
                result
            }
            else -> null
        }
    }

    private fun parseData(event: String, json: String): SseEvent {
        return when (event) {
            "text" -> {
                val parsed = mapAdapter.fromJson(json)
                val text = parsed?.get("text") as? String ?: ""
                SseEvent.TextChunk(text)
            }
            "action" -> {
                val parsed = mapAdapter.fromJson(json) ?: emptyMap()
                val actionType = parsed["type"] as? String ?: ""
                val actionId = parsed["actionId"] as? String
                val rawPayload = parsed["payload"] as? Map<*, *>
                val payload = mutableMapOf<String, Any>()
                rawPayload?.forEach { (key, value) ->
                    if (key is String && value != null) {
                        payload[key] = value
                    }
                }
                SseEvent.Action(actionId, actionType, payload)
            }
            "metadata" -> {
                val metadata = kkypAdapter.fromJson(json)
                if (metadata != null) {
                    SseEvent.Metadata(metadata)
                } else {
                    SseEvent.Error("Geçersiz metadatalı KKYP verisi")
                }
            }
            "done" -> {
                SseEvent.Done
            }
            "error" -> {
                val parsed = mapAdapter.fromJson(json)
                val errorMsg = parsed?.get("message") as? String ?: "Bilinmeyen sunucu hatası"
                SseEvent.Error(errorMsg)
            }
            else -> {
                SseEvent.Error("Bilinmeyen event tipi: $event")
            }
        }
    }
}
