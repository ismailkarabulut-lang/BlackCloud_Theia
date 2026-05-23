// === app/src/main/java/com/blackcloud/shell/data/api/TheiaApiClient.kt ===
package com.blackcloud.shell.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * PROJECT THEIA BLACKCLOUD API istemcisi.
 * HTTP bağlantı ayarlarını ve Retrofit servis örneğini yönetir.
 */
object TheiaApiClient {

    private var currentBaseUrl = "http://10.202.60.1:8000/api/"
    private var currentService: TheiaApiService? = null
    private var currentApiKey: String? = null
    private var currentModelName: String = "deep_synthesis_v4"

    val service: TheiaApiService
        get() {
            return currentService ?: synchronized(this) {
                currentService ?: buildService(currentBaseUrl).also { currentService = it }
            }
        }

    fun updateBaseUrl(newUrl: String) {
        val sanitized = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        if (currentBaseUrl != sanitized) {
            currentBaseUrl = sanitized
            synchronized(this) {
                currentService = buildService(sanitized)
             }
        }
    }

    fun getBaseUrl(): String = currentBaseUrl

    fun updateApiKey(key: String) {
        currentApiKey = key
    }

    fun getApiKey(): String? = currentApiKey

    fun updateModelName(model: String) {
        currentModelName = model
    }

    fun getModelName(): String = currentModelName

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES) // Stream uzun sürebilir
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                val currentKey = getApiKey()
                if (!currentKey.isNullOrEmpty()) {
                    builder.addHeader("Authorization", "Bearer $currentKey")
                }
                builder.addHeader("X-Model-Name", getModelName())
                chain.proceed(builder.build())
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            })
            .build()
    }

    private fun buildService(url: String): TheiaApiService {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TheiaApiService::class.java)
    }
}
