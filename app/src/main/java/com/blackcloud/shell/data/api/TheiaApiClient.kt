// === app/src/main/java/com/blackcloud/shell/data/api/TheiaApiClient.kt ===
package com.blackcloud.shell.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * PROJECT THEIA BLACKCLOUD API istemcisi.
 * HTTP bağlantı ayarlarını ve Retrofit servis örneğini yönetir.
 */
object TheiaApiClient {

    // VM IP'si dynamic — VM yeniden başlarsa bu değişebilir, o zaman buradan güncelle.
    private const val BASE_URL = "http://10.202.60.1:8000/api/"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES) // Stream uzun sürebilir
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            })
            .build()
    }

    val service: TheiaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TheiaApiService::class.java)
    }
}