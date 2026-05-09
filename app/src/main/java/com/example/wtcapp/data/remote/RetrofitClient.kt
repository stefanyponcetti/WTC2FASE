package com.example.wtcapp.data.remote

import android.os.Build
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5255/"

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

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    val chatApi: ChatApiService by lazy {
        retrofit.create(ChatApiService::class.java)
    }

    val passwordApi: RedefinirSenhaApi by lazy {
        retrofit.create(RedefinirSenhaApi::class.java)
    }
}
