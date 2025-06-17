package com.example.championcart.di

import android.content.Context
import com.example.championcart.data.api.ChampionCartApi
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private var tokenManager: TokenManager? = null
    private var isInitialized = false

    fun initialize(context: Context) {
        if (!isInitialized) {
            tokenManager = TokenManager(context.applicationContext)
            isInitialized = true
        }
    }

    private fun getTokenManager(): TokenManager {
        return tokenManager ?: throw IllegalStateException("NetworkModule not initialized. Call initialize() first.")
    }

    private val authInterceptor by lazy {
        Interceptor { chain ->
            val original = chain.request()
            val token = try {
                getTokenManager().getToken()
            } catch (e: Exception) {
                null
            }

            val request = if (token != null &&
                !original.url.encodedPath.contains("login") &&
                !original.url.encodedPath.contains("register")) {
                original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("Content-Type", "application/json")
                    .build()
            } else {
                original.newBuilder()
                    .header("Content-Type", "application/json")
                    .build()
            }

            chain.proceed(request)
        }
    }

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val debugInterceptor by lazy {
        Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)

            // Log the URL and response code
            println("==> API Request: ${request.method} ${request.url}")
            println("<== API Response: ${response.code} from ${response.request.url}")

            response
        }
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(debugInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ============ SINGLE API INTERFACE ============
    val api: ChampionCartApi by lazy {
        retrofit.create(ChampionCartApi::class.java)
    }
}