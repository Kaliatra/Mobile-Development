package com.dicoding.kaliatra.ui.dictionary.data.retrofit

import com.dicoding.kaliatra.ui.dictionary.data.response.DataAllResponseItem
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("api/entry")
    suspend fun getAllDictionaryEntries(): Response<List<DataAllResponseItem>>
}