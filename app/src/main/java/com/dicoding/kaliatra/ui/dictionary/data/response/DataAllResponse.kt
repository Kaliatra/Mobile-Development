package com.dicoding.kaliatra.ui.dictionary.data.response

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class DataAllResponseItem(
	@SerializedName("id") val id: String,
	@SerializedName("aksara") val aksara: String,
	@SerializedName("tulisanlatin") val tulisanlatin: String,
	@SerializedName("deskripsi") val deskripsi: String,
	@SerializedName("category") val category: String,
	@SerializedName("imageUrl") val imageUrl: String,
	@SerializedName("createdAt") val createdAt: Timestamp
)

