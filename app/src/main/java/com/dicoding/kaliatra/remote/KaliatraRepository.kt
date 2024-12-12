package com.dicoding.kaliatra.remote

import com.dicoding.kaliatra.remote.response.DeleteResponse
import com.dicoding.kaliatra.remote.response.DictionaryResponse
import com.dicoding.kaliatra.remote.response.HandwritingResponse
import com.dicoding.kaliatra.remote.response.HistoryData
import com.dicoding.kaliatra.remote.response.LastPredictionResponse
import com.dicoding.kaliatra.remote.response.PredictionResponse
import com.dicoding.kaliatra.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Response

class KaliatraRepository(private val apiService: ApiService) {

    suspend fun getAllDictionaryEntries(): Response<List<DictionaryResponse>> {
        return apiService.getAllDictionaryEntries()
    }

    suspend fun uploadHandwriting(
        authToken: String,
        file: MultipartBody.Part
    ): Response<HandwritingResponse> {
        return apiService.handwritingAksara(authToken, file)
    }

    suspend fun predictAksara(
        token: String,
        file: MultipartBody.Part
    ): Response<PredictionResponse> {
        return apiService.predictAksara(token, file)
    }

    suspend fun getLatestPrediction(token: String): Response<LastPredictionResponse> {
        return withContext(Dispatchers.IO) {
            apiService.getLatestPrediction("Bearer $token")
        }
    }

    suspend fun getPredictionHistories(authorization: String): Result<List<HistoryData>> {
        return try {
            val response = apiService.getPredictionHistories(authorization)
            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception("Failed to load histories"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePredictionHistory(id: String, authorization: String): Result<DeleteResponse> {
        return try {
            val response = apiService.deletePredictionHistory(id, authorization)

            if (response.isSuccessful) {
                val deleteResponse = DeleteResponse(
                    message = "History deleted successfully",
                    status = "success"
                )
                Result.success(deleteResponse)
            } else {
                Result.failure(Exception("Failed to delete history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
