// === app/src/main/java/com/blackcloud/shell/data/model/Project.kt ===
package com.blackcloud.shell.data.model

import com.squareup.moshi.JsonClass

/**
 * Proje verilerini temsil eden veri sınıfı.
 * BLACKCLOUD üzerinde oluşturulan bilişsel projeleri içerir.
 */
@JsonClass(generateAdapter = true)
data class Project(
    val id: String,
    val name: String,
    val tags: List<String>,
    val createdAt: String
)
