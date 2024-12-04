package com.dicoding.kaliatra.ui.dictionary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.kaliatra.R
import com.dicoding.kaliatra.ui.dictionary.data.response.DataAllResponseItem
import com.dicoding.kaliatra.ui.dictionary.data.retrofit.ApiConfig
import com.dicoding.kaliatra.ui.dictionary.data.retrofit.ApiService
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class DictionaryViewModel(application: Application) : AndroidViewModel(application) {

    private val _filteredEntries = MutableLiveData<List<DictionaryItem>>()
    val filteredEntries: LiveData<List<DictionaryItem>> get() = _filteredEntries

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val apiService: ApiService = ApiConfig.getApiService()

    private var allEntries = emptyList<DataAllResponseItem>()

    fun fetchAllDictionaryEntries() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response: Response<List<DataAllResponseItem>> = withContext(Dispatchers.IO) {
                    apiService.getAllDictionaryEntries()
                }

                if (response.isSuccessful) {
                    allEntries = response.body() ?: emptyList()

                    val grouped = allEntries.groupBy { it.category }.flatMap {
                        listOf(
                            DictionaryItem.CategoryHeader(it.key),
                            *it.value.map { entry -> DictionaryItem.DictionaryEntry(entry) }.toTypedArray()
                        )
                    }

                    _filteredEntries.value = grouped
                } else {
                    _errorMessage.value = getApplication<Application>().getString(R.string.failed_to_load_data)
                }
            } catch (e: Exception) {
                _errorMessage.value = getApplication<Application>().getString(R.string.unknown_error)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterDictionaryEntries(query: String) {
        if (query.isBlank()) {
            _filteredEntries.value = allEntries.groupBy { it.category }.flatMap {
                listOf(
                    DictionaryItem.CategoryHeader(it.key),
                    *it.value.map { entry -> DictionaryItem.DictionaryEntry(entry) }.toTypedArray()
                )
            }
            return
        }

        val filteredList = allEntries.filter {
            it.tulisanlatin.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }

        val sortedList = filteredList.sortedWith(compareByDescending<DataAllResponseItem> {
            it.tulisanlatin.equals(query, ignoreCase = true)
        }.thenByDescending {
            it.tulisanlatin.startsWith(query, ignoreCase = true)
        }.thenBy {
            it.tulisanlatin.indexOf(query, ignoreCase = true)
        }.thenBy {
            it.tulisanlatin
        })

        val grouped = sortedList.groupBy { it.category }.flatMap {
            listOf(
                DictionaryItem.CategoryHeader(it.key),
                *it.value.map { entry -> DictionaryItem.DictionaryEntry(entry) }.toTypedArray()
            )
        }

        _filteredEntries.value = grouped
    }
}
