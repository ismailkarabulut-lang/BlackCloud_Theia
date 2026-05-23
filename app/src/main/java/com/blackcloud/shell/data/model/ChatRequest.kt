// === app/src/main/java/com/blackcloud/shell/data/model/ChatRequest.kt ===
package com.blackcloud.shell.data.model

import com.squareup.moshi.JsonClass

/**
 * Sohbet isteği veri sınıfı.
 * Arka plana gönderilen mesaj, ilgili proje kimliği ve oturum kimliğini taşır.
 * model: Aktif seçili model sağlayıcısının kimliği (varsayılan: ModelManager'dan gelir).
 */
@JsonClass(generateAdapter = true)
data class ChatRequest(
    val projectId: String?,
    val message: String,
    val sessionId: String,
    val model: String = ModelManager.getActiveModel().modelId
)
