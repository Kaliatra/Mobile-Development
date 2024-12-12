package com.dicoding.kaliatra.remote.response

import com.google.gson.annotations.SerializedName

data class LastPredictionResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: LastPredictionData
)

data class LastPredictionData(
    @SerializedName("id")
    val id: String,
    @SerializedName("topPredictions")
    val topPredictions: List<String>,
    @SerializedName("imageURL")
    val imageURL: String,
    @SerializedName("createdAt")
    val createdAt: String
)