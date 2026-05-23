package com.blackcloud.shell.data.model

/**
 * Model sağlayıcılarını temsil eden veri sınıfı.
 */
data class ModelProvider(
    val modelId: String,
    val displayName: String,
    val description: String,
    val isFree: Boolean
)
