package com.dicoding.kaliatra.ui.scan

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.dicoding.kaliatra.di.Injection
import com.dicoding.kaliatra.remote.response.PredictionResponse
import com.dicoding.kaliatra.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ScanViewModel : ViewModel() {

    private val repository = Injection.providePredictionRepository()

    private suspend fun getFirebaseToken(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.e("FirebaseToken", "User is not authenticated")
            return null
        }
        val tokenResult = user.getIdToken(false).await()
        val token = tokenResult?.token
        Log.d("FirebaseToken", "Retrieved token: $token")
        return token
    }

    fun analyzeImage(context: Context, imageUri: Uri, onSuccess: (PredictionResponse) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val compressedImage = Utils.compressImage(context, imageUri)
            val file = File(context.cacheDir, "compressed_image.jpg")
            file.writeBytes(compressedImage)

            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val token = getFirebaseToken()
            if (token != null) {
                val response = repository.predictAksara("Bearer $token", body)
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    Log.e(
                        "com.dicoding.kaliatra.ui.scan.ScanViewModel",
                        "Error: ${response.errorBody()}"
                    )
                }
            } else {
                Log.e(
                    "com.dicoding.kaliatra.ui.scan.ScanViewModel",
                    "Error: Unable to get Firebase token"
                )
            }
        }
    }
}