package com.dicoding.kaliatra.remote

import com.dicoding.kaliatra.remote.response.DataAllResponseItem
import com.dicoding.kaliatra.remote.retrofit.ApiService
import retrofit2.Response

class KaliatraRepository(private val apiService: ApiService) {

    suspend fun getAllDictionaryEntries(): Response<List<DataAllResponseItem>> {
        return apiService.getAllDictionaryEntries()
    }
}
