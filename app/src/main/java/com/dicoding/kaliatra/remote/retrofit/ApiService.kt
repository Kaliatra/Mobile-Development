package com.dicoding.kaliatra.remote.retrofit

import com.dicoding.kaliatra.remote.response.DataAllResponseItem
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("api/entry")
    suspend fun getAllDictionaryEntries(): Response<List<DataAllResponseItem>>
}