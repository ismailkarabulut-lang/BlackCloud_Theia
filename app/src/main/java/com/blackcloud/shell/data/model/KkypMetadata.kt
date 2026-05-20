// === app/src/main/java/com/blackcloud/shell/data/model/KkypMetadata.kt ===
package com.blackcloud.shell.data.model

import com.squareup.moshi.JsonClass

/**
 * KKYP (Kişisel Bilgi Güvenliği Doğrulama) meta verilerini temsil eder.
 * Yanıtın doğrulanıp doğrulanmadığını ve varsa tespit edilen sorunları saklar.
 */
@JsonClass(generateAdapter = true)
data class KkypMetadata(
    val verified: Boolean,
    val issues: List<String>
)
