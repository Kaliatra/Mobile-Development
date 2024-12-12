package com.dicoding.kaliatra.ui.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.kaliatra.di.Injection

@Suppress("UNCHECKED_CAST")
class HistoryViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = Injection.provideHistoryRepository()
        return HistoryViewModel(application, repository) as T
    }
}
