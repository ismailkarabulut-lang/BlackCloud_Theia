// === app/src/main/java/com/blackcloud/shell/data/model/SseEvent.kt ===
package com.blackcloud.shell.data.model

/**
 * Sunucu Tarafı Etkinlikleri (SSE) için mühürlü sınıf (sealed class).
 * Backend'den gelen akış verilerini beş farklı kategoride gruplandırır.
 */
sealed class SseEvent {

    /**
     * Asistan cevabının bir parçasını temsil eder.
     */
    data class TextChunk(val text: String) : SseEvent()

    /**
     * Backend'in cihaz üzerinde çalıştırmak istediği komutu (eylemi) tanımlar.
     */
    data class Action(
        val actionId: String?,
        val actionType: String,
        val payload: Map<String, Any>
    ) : SseEvent()

    /**
     * KKYP doğrulama meta verilerini içerir. Akış sonunda gelir.
     */
    data class Metadata(val kkyp: KkypMetadata) : SseEvent()

    /**
     * Akışın başarıyla tamamlandığını bildiren sinyal.
     */
    object Done : SseEvent()

    /**
     * Akış sırasında veya bağlantıda oluşan hata bildirimi.
     */
    data class Error(val message: String) : SseEvent()
}
