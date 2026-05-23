// === app/src/main/java/com/blackcloud/shell/data/api/TheiaApiService.kt ===
package com.blackcloud.shell.data.api

import com.blackcloud.shell.data.model.ChatRequest
import com.blackcloud.shell.data.model.Project
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming

/**
 * PROJECT THEIA BLACKCLOUD API'sine erişim arayüzü (Retrofit).
 * Yerel sunucuyla (Termux localhost) tüm iletişimi sağlar.
 */
interface TheiaApiService {

    @Streaming
    @POST("chat/stream")
    suspend fun streamChat(
        @Body request: ChatRequest
    ): ResponseBody

    @GET("health")
    suspend fun ping(): Response<Void>

    @GET("projects")
    suspend fun getProjects(): List<Project>

    @GET("projects/active")
    suspend fun getActiveProject(): Project

    @POST("action/result")
    suspend fun reportActionResult(
        @retrofit2.http.Body result: ActionResult
    ): Response<Void>
}

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class ActionResult(
    val actionId: String,
    val success: Boolean,
    val message: String
)
