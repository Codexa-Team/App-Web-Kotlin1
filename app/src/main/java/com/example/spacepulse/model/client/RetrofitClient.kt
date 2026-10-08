package com.example.spacepulse.model.client

import com.example.spacepulse.model.response.WebService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // 10.0.2.2 es la IP del host (tu PC) vista desde el emulador oficial de Android Studio
    // 192.168.18.85 es la IP de tu PC en la red local Wi-Fi (para probar en celular físico)
    const val EMULATOR_BASE_URL = "http://10.0.2.2:8080/"
    const val LAN_BASE_URL = "http://192.168.18.85:8080/"

    private var currentBaseUrl = EMULATOR_BASE_URL

    var authToken: String? = null

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()

        // Si ya tiene header Authorization manual, lo respeta. De lo contrario, inyecta el token guardado.
        if (original.header("Authorization") == null && !authToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $authToken")
        }

        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private var retrofit: Retrofit = buildRetrofit(currentBaseUrl)

    private fun buildRetrofit(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    var webService: WebService = retrofit.create(WebService::class.java)
        private set

    fun setBaseUrl(newUrl: String) {
        val sanitizedUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        currentBaseUrl = sanitizedUrl
        retrofit = buildRetrofit(sanitizedUrl)
        webService = retrofit.create(WebService::class.java)
    }

    fun getBaseUrl(): String = currentBaseUrl

    /**
     * Resuelve las URLs de imágenes que el backend devuelve como http://localhost:8080/...
     * para que apunten al host correcto accesible desde el emulador o celular.
     */
    fun resolveImageUrl(rawUrl: String?): String? {
        if (rawUrl.isNullOrBlank()) return null
        val hostPart = currentBaseUrl.removeSuffix("/")
        return rawUrl
            .replace("http://localhost:8080", hostPart)
            .replace("http://127.0.0.1:8080", hostPart)
    }
}