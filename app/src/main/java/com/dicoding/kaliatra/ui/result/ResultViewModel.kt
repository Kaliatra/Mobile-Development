package com.dicoding.kaliatra.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.kaliatra.di.Injection
import com.dicoding.kaliatra.remote.response.LastPredictionResponse
import kotlinx.coroutines.launch

class ResultViewModel : ViewModel() {

    private val repository = Injection.provideResultRepository()

    private val _lastPrediction = MutableLiveData<LastPredictionResponse>()
    val lastPrediction: LiveData<LastPredictionResponse> = _lastPrediction

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getLatestPrediction(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getLatestPrediction(token)
                if (response.isSuccessful) {
                    _lastPrediction.postValue(response.body())
                } else {
                    _errorMessage.postValue("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Exception: ${e.message}")
            }
        }
    }
}
