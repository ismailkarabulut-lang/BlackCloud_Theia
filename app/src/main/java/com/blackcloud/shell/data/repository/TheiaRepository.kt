// === app/src/main/java/com/blackcloud/shell/data/repository/TheiaRepository.kt ===
package com.blackcloud.shell.data.repository

import com.blackcloud.shell.data.api.TheiaApiClient
import com.blackcloud.shell.data.api.TheiaApiService
import com.blackcloud.shell.data.model.ChatRequest
import com.blackcloud.shell.data.model.Project
import com.blackcloud.shell.data.model.SseEvent
import com.blackcloud.shell.data.sse.SseParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException

/**
 * PROJECT THEIA BLACKCLOUD Veri Deposu (Repository) Sınıfı.
 * SSE akışını ve diğer suspend API çağrılarını yönetir.
 */
class TheiaRepository(
    private val apiService: TheiaApiService = TheiaApiClient.service
) {

    /**
     * Yerel sunucuya ping atarak bağlantının durumunu test eder.
     * @return Bağlantı başarılıysa true, aksi halde false.
     */
    suspend fun ping(): Boolean {
        return try {
            val response = apiService.ping()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Tüm projeleri sunucudan getirir.
     */
    suspend fun getProjects(): List<Project> {
        return try {
            apiService.getProjects()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Aktif seçili projeyi getirir.
     */
    suspend fun getActiveProject(): Project? {
        return try {
            apiService.getActiveProject()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Eylem sonucunu sunucuya rapor eder.
     */
    suspend fun reportActionResult(actionId: String, success: Boolean, message: String): Boolean {
        return try {
            val response = apiService.reportActionResult(
                com.blackcloud.shell.data.api.ActionResult(actionId, success, message)
            )
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Sohbet akışını SSE (Server-Sent Events) protokolü üzerinden dinler.
     * Cold Flow olarak çalışır, coroutine iptallerine duyarlıdır ve IO thread'inde çalıştırılır.
     */
    fun streamChat(request: ChatRequest): Flow<SseEvent> = flow {
        val parser = SseParser()
        try {
            val responseBody = apiService.streamChat(request)
            val source = responseBody.source()

            while (!source.exhausted()) {
                val line = source.readUtf8Line()
                if (line != null) {
                    val event = parser.parseLine(line)
                    if (event != null) {
                        emit(event)
                        if (event is SseEvent.Done) {
                            break
                        }
                    }
                }
            }
        } catch (e: IOException) {
            emit(SseEvent.Error("Ağ bağlantısı koptu veya sunucuya erişilemiyor: ${e.message}"))
        } catch (e: Exception) {
            emit(SseEvent.Error("Sohbet akışı hatası: ${e.message}"))
        }
    }.cancellable().flowOn(Dispatchers.IO)
}
