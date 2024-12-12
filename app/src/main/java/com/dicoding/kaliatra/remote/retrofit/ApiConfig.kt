package com.dicoding.kaliatra.remote.retrofit

import com.dicoding.kaliatra.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {

    private fun getFirebaseToken(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return try {
            val result = user?.getIdToken(true)?.result
            result?.token
        } catch (e: Exception) {
            null
        }
    }

    private fun getClient(): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                val token = getFirebaseToken()
                token?.let {
                    request.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(request.build())
            }
        return clientBuilder.build()
    }

    fun getKaliatraApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.KALIATRA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build()
        return retrofit.create(ApiService::class.java)
    }

    fun getPredictionApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.PREDICTION_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build()
        return retrofit.create(ApiService::class.java)
    }
}