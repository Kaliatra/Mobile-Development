package com.dicoding.kaliatra.remote.retrofit

import com.dicoding.kaliatra.remote.response.DictionaryResponse
import com.dicoding.kaliatra.remote.response.HandwritingResponse
import com.dicoding.kaliatra.remote.response.HistoryResponse
import com.dicoding.kaliatra.remote.response.LastPredictionResponse
import com.dicoding.kaliatra.remote.response.PredictionResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @Multipart
    @POST("predict")
    suspend fun predictAksara(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<PredictionResponse>

    @Multipart
    @POST("/predict/handwrite")
    suspend fun handwritingAksara(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<HandwritingResponse>

    @GET("/predict/latest")
    suspend fun getLatestPrediction(
        @Header("Authorization") token: String
    ): Response<LastPredictionResponse>

    @GET("/predict/histories")
    suspend fun getPredictionHistories(
        @Header("Authorization") token: String
    ): Response<HistoryResponse>

    @DELETE("/predict/histories/{id}")
    suspend fun deletePredictionHistory(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Response<Unit>

    @GET("api/entry")
    suspend fun getAllDictionaryEntries(): Response<List<DictionaryResponse>>
}