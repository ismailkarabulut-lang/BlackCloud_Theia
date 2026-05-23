// === app/src/main/java/com/blackcloud/shell/data/model/ModelManager.kt ===
package com.blackcloud.shell.data.model

import android.content.Context
import android.content.SharedPreferences

object ModelManager {
    private const val PREFS_NAME = "theia_model_prefs"
    private const val KEY_ACTIVE_MODEL = "active_model"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getActiveModel(): ModelProvider {
        val name = prefs.getString(KEY_ACTIVE_MODEL, ModelProvider.GEMINI_FLASH.name)
        return try {
            ModelProvider.valueOf(name!!)
        } catch (e: Exception) {
            ModelProvider.GEMINI_FLASH
        }
    }

    fun setActiveModel(provider: ModelProvider) {
        prefs.edit().putString(KEY_ACTIVE_MODEL, provider.name).apply()
    }

    fun getAllModels(): List<ModelProvider> = ModelProvider.values().toList()
}
