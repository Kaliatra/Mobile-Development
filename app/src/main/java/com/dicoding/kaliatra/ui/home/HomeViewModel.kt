package com.dicoding.kaliatra.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.kaliatra.di.Injection
import com.dicoding.kaliatra.remote.KaliatraRepository
import com.dicoding.kaliatra.remote.response.HandwritingResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KaliatraRepository = Injection.provideHandwritingRepository()

    private val _handwritingResponse = MutableLiveData<HandwritingResponse?>()
    val handwritingResponse: MutableLiveData<HandwritingResponse?> get() = _handwritingResponse

    private val _errorStatus = MutableLiveData<String?>()
    val errorStatus: LiveData<String?> = _errorStatus

    fun resetHandwritingResponse() {
        _handwritingResponse.value = null
        _errorStatus.value = null
    }

    fun uploadHandwriting(authToken: String, file: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response: Response<HandwritingResponse> = repository.uploadHandwriting(authToken, file)
                if (response.isSuccessful) {
                    _handwritingResponse.value = response.body()
                } else {
                    _errorStatus.postValue("Error: ${response.message()}")
                    _handwritingResponse.value = HandwritingResponse(
                        status = "fail",
                        message = "Error: ${response.message()}",
                        data = null
                    )
                }
            } catch (e: Exception) {
                _errorStatus.postValue("Failure: ${e.localizedMessage}")
                _handwritingResponse.value = HandwritingResponse(
                    status = "fail",
                    message = "Failure: ${e.localizedMessage}",
                    data = null
                )
            }
        }
    }
}