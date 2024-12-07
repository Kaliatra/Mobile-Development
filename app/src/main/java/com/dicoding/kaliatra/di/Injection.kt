package com.dicoding.kaliatra.di

import com.dicoding.kaliatra.remote.KaliatraRepository
import com.dicoding.kaliatra.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(): KaliatraRepository {
        val apiService = ApiConfig.getApiService()
        return KaliatraRepository(apiService)
    }
}
