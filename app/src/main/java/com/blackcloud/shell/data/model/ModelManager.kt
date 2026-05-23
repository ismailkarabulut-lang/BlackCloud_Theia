// app/src/main/java/com/blackcloud/shell/data/model/ModelManager.kt
package com.blackcloud.shell.data.model

/**
 * Kullanılabilir modelleri ve aktif seçimi yöneten singleton.
 * Aktif model değiştiğinde TheiaApiClient otomatik güncellenir.
 */
object ModelManager {

    private val models = listOf(
        ModelProvider(
            modelId = "deep_synthesis_v4",
            displayName = "Deep Synthesis v4",
            description = "Varsayılan yerel model",
            isFree = true
        ),
        ModelProvider(
            modelId = "gemini-2.0-flash",
            displayName = "Gemini 2.0 Flash",
            description = "Google — hızlı ve ücretsiz",
            isFree = true
        ),
        ModelProvider(
            modelId = "gemini-2.5-pro",
            displayName = "Gemini 2.5 Pro",
            description = "Google — gelişmiş akıl yürütme",
            isFree = false
        ),
        ModelProvider(
            modelId = "claude-sonnet-4-6",
            displayName = "Claude Sonnet 4.6",
            description = "Anthropic — dengeli ve güçlü",
            isFree = false
        ),
        ModelProvider(
            modelId = "gpt-4o-mini",
            displayName = "GPT-4o Mini",
            description = "OpenAI — hızlı ve ekonomik",
            isFree = false
        )
    )

    private var activeModel: ModelProvider = models.first()

    fun getAllModels(): List<ModelProvider> = models

    fun getActiveModel(): ModelProvider = activeModel

    fun setActiveModel(model: ModelProvider) {
        activeModel = model
    }

    fun findById(modelId: String): ModelProvider? =
        models.find { it.modelId == modelId }
}
