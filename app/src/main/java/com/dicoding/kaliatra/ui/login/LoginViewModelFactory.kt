package com.dicoding.kaliatra.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModelFactory(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(auth, firestore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}