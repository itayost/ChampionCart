package com.example.championcart.di

import com.example.championcart.data.api.ChampionCartApi
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

// Qualifiers to distinguish different interceptors
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DebugInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @AuthInterceptor
    fun provideAuthInterceptor(tokenManager: TokenManager): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val token = try {
                tokenManager.getToken()
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

    @Provides
    @Singleton
    @LoggingInterceptor
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @DebugInterceptor
    fun provideDebugInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)

            // Log the URL and response code
            println("==> API Request: ${request.method} ${request.url}")
            println("<== API Response: ${response.code} from ${response.request.url}")

            response
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @AuthInterceptor authInterceptor: Interceptor,
        @LoggingInterceptor loggingInterceptor: HttpLoggingInterceptor,
        @DebugInterceptor debugInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(debugInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideChampionCartApi(retrofit: Retrofit): ChampionCartApi {
        return retrofit.create(ChampionCartApi::class.java)
    }
}