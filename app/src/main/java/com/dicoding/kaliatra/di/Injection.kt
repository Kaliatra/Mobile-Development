package com.dicoding.kaliatra.di

import com.dicoding.kaliatra.remote.KaliatraRepository
import com.dicoding.kaliatra.remote.retrofit.ApiConfig

object Injection {

    fun provideHandwritingRepository(): KaliatraRepository {
        val apiService = ApiConfig.getPredictionApiService()
        return KaliatraRepository(apiService)
    }

    fun providePredictionRepository(): KaliatraRepository {
        val apiService = ApiConfig.getPredictionApiService()
        return KaliatraRepository(apiService)
    }

    fun provideResultRepository(): KaliatraRepository {
        val apiService = ApiConfig.getPredictionApiService()
        return KaliatraRepository(apiService)
    }

    fun provideDictionaryRepository(): KaliatraRepository {
        val apiService = ApiConfig.getKaliatraApiService()
        return KaliatraRepository(apiService)
    }

    fun provideHistoryRepository(): KaliatraRepository {
        val apiService = ApiConfig.getPredictionApiService()
        return KaliatraRepository(apiService)
    }
}