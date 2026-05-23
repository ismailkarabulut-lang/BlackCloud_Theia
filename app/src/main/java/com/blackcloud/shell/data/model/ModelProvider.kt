// app/src/main/java/com/blackcloud/shell/data/model/ModelProvider.kt
package com.blackcloud.shell.data.model

/**
 * Tek bir AI model sağlayıcısını temsil eder.
 * isFree: API key gerektirmeden kullanılabilir mi?
 */
data class ModelProvider(
    val modelId: String,        // Backend'e gönderilen teknik ID
    val displayName: String,    // UI'da gösterilen isim
    val description: String,    // Kısa açıklama
    val isFree: Boolean = false
)
