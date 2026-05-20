// === app/src/main/java/com/blackcloud/shell/data/model/ChatRequest.kt ===
package com.blackcloud.shell.data.model

import com.squareup.moshi.JsonClass

/**
 * Sohbet isteği veri sınıfı.
 * Arka plana gönderilen mesaj, ilgili proje kimliği ve oturum kimliğini taşır.
 */
@JsonClass(generateAdapter = true)
data class ChatRequest(
    val projectId: String?,
    val message: String,
    val sessionId: String
)
