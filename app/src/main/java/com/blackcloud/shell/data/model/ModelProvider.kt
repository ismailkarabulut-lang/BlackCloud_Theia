// === app/src/main/java/com/blackcloud/shell/data/model/ModelProvider.kt ===
package com.blackcloud.shell.data.model

enum class ModelProvider(
    val displayName: String,
    val description: String,
    val baseUrl: String,
    val modelId: String,
    val isFree: Boolean
) {
    GEMINI_FLASH(
        displayName = "Gemini 2.0 Flash",
        description = "Hızlı & Ücretsiz",
        baseUrl = "http://10.202.60.1:8000/api/",
        modelId = "gemini-2.0-flash",
        isFree = true
    ),
    GEMINI_PRO(
        displayName = "Gemini 1.5 Pro",
        description = "Derin Analiz",
        baseUrl = "http://10.202.60.1:8000/api/",
        modelId = "gemini-1.5-pro",
        isFree = true
    ),
    CLAUDE_SONNET(
        displayName = "Claude Sonnet",
        description = "Nöral Sentez & Muhakeme",
        baseUrl = "http://10.202.60.1:8000/api/",
        modelId = "claude-sonnet-4-5",
        isFree = false
    ),
    DEEPSEEK_V3(
        displayName = "DeepSeek V3",
        description = "Kod & Mantık",
        baseUrl = "http://10.202.60.1:8000/api/",
        modelId = "deepseek-chat",
        isFree = true
    ),
    OLLAMA_LOCAL(
        displayName = "Llama 3.2 Local",
        description = "Çevrimdışı / Yerel",
        baseUrl = "http://10.202.60.1:8000/api/",
        modelId = "llama3.2",
        isFree = true
    )
}
