package com.example.wtcapp.data.remote

import android.os.Build
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://wtc2fase-net.onrender.com/"
    private var _api: UserApi? = null
    private var _chatApi: ChatApiService? = null
    private var _passwordApi: RedefinirSenhaApi? = null

    fun isEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.lowercase().contains("vbox") ||
            Build.FINGERPRINT.lowercase().contains("test-keys") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for x86") ||
            Build.MANUFACTURER.contains("Genymotion") ||
            (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
            Build.PRODUCT == "google_sdk"
    }

    fun initialize(onUnauthorized: () -> Unit) {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(onUnauthorized))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        _api = retrofit.create(UserApi::class.java)
        _chatApi = retrofit.create(ChatApiService::class.java)
        _passwordApi = retrofit.create(RedefinirSenhaApi::class.java)
    }

    val api: UserApi
        get() = _api ?: error("RetrofitClient não inicializado. Chame initialize() primeiro.")

    val chatApi: ChatApiService
        get() = _chatApi ?: error("RetrofitClient não inicializado. Chame initialize() primeiro.")

    val passwordApi: RedefinirSenhaApi
        get() = _passwordApi ?: error("RetrofitClient não inicializado. Chame initialize() primeiro.")
}
