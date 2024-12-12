package com.dicoding.kaliatra.remote.response

import com.google.gson.annotations.SerializedName

data class HandwritingResponse(
    @field:SerializedName("data")
    val data: HandwritingData?,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("status")
    val status: String
)

data class HandwritingData(
    @field:SerializedName("createdAt")
    val createdAt: String,
    @field:SerializedName("topPredictions")
    val topPredictions: List<String>,
    @field:SerializedName("imageURL")
    val imageURL: String,
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("userId")
    val userId: String
)