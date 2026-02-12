package com.example.navya.di

import com.example.navya.data.remote.ApiService
import com.example.navya.utils.SupabaseConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val headerInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder =
                    original.newBuilder().header("apikey", SupabaseConfig.SUPABASE_ANON_KEY)

            if (original.header("Authorization") == null) {
                requestBuilder.header("Authorization", "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}")
            }

            chain.proceed(requestBuilder.build())
        }

        return OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .addInterceptor(logging)
                .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        val baseUrl =
                if (SupabaseConfig.SUPABASE_URL.endsWith("/")) {
                    "${SupabaseConfig.SUPABASE_URL}rest/v1/"
                } else {
                    "${SupabaseConfig.SUPABASE_URL}/rest/v1/"
                }

        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
