package com.dicoding.kaliatra.ui.history

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.kaliatra.R
import com.dicoding.kaliatra.remote.KaliatraRepository
import com.dicoding.kaliatra.remote.response.HistoryData
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application, private val repository: KaliatraRepository) :
    AndroidViewModel(application) {

    private val _historyList = MutableLiveData<List<HistoryData>>()
    val historyList: LiveData<List<HistoryData>> = _historyList

    private val _loadingStatus = MutableLiveData<Boolean>()
    val loadingStatus: LiveData<Boolean> = _loadingStatus

    private val _deleteStatus = MutableLiveData<String>()
    val deleteStatus: LiveData<String> = _deleteStatus

    fun getPredictionHistories(authorization: String) {
        viewModelScope.launch {
            _loadingStatus.postValue(true)
            val result = repository.getPredictionHistories(authorization)
            _loadingStatus.postValue(false)
            if (result.isSuccess) {
                _historyList.postValue(result.getOrNull()!!)
            }
        }
    }

    fun deletePredictionHistory(id: String, authorization: String, context: Context) {
        viewModelScope.launch {
            val result = repository.deletePredictionHistory(id, authorization)
            if (result.isSuccess) {
                val successMessage = context.getString(R.string.delete_successful)
                _deleteStatus.postValue(successMessage)

                _historyList.value = _historyList.value?.filter { it.id != id }
            } else {
                val failureMessage = context.getString(R.string.delete_failed)
                _deleteStatus.postValue(failureMessage)
            }
        }
    }

}