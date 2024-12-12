package com.dicoding.kaliatra.remote.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(
    @field:SerializedName("data")
    val data: List<HistoryData>,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("status")
    val status: String
)

data class HistoryData(
    @field:SerializedName("createdAt")
    val createdAt: String,
    @field:SerializedName("topPredictions")
    val topPredictions: List<String>,
    @field:SerializedName("imageURL")
    val imageURL: String,
    @field:SerializedName("id")
    val id: String
)
