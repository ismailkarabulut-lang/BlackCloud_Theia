package com.blackcloud.shell.data.model

import com.blackcloud.shell.data.api.TheiaApiClient

/**
 * Siber modellerin yönetimini üstlenen merkezî yöneticidir.
 */
object ModelManager {
    private val models = listOf(
        ModelProvider("deep_synthesis_v4", "Deep_Synthesis_v4", "Nöral Sentez & Kod Geliştirme", true),
        ModelProvider("linguist_mainframe", "Linguist_Mainframe", "Doğal Dil İşleme & Çeviri", true),
        ModelProvider("vision_oracle_x", "Vision_Oracle_X", "Görüntü İşleme & Analiz", false),
        ModelProvider("cognitive_aura_v2", "Cognitive_Aura_v2", "Semantik Karar Verme", false)
    )

    fun getAllModels(): List<ModelProvider> = models

    fun getActiveModel(): ModelProvider {
        val currentId = TheiaApiClient.getModelName()
        return models.firstOrNull { it.modelId.equals(currentId, ignoreCase = true) } ?: models.first()
    }

    fun setActiveModel(model: ModelProvider) {
        TheiaApiClient.updateModelName(model.modelId)
    }
}
