package com.dicoding.kaliatra.remote.response

import com.google.gson.annotations.SerializedName

data class PredictionResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: PredictionData
)

data class PredictionData(
    @SerializedName("id")
    val id: String,
    @SerializedName("topPredictions")
    val topPredictions: List<String>,
    @SerializedName("createdAt")
    val createdAt: String
)