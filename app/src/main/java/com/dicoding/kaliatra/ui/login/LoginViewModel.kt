package com.dicoding.kaliatra.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _loginResult = MutableLiveData<FirebaseUser?>()
    val loginResult: LiveData<FirebaseUser?> get() = _loginResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun signInWithGoogle(idToken: String) {
        _loading.value = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            _loading.value = false
            if (task.isSuccessful) {
                _loginResult.value = auth.currentUser

                val user = auth.currentUser
                user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val token = tokenTask.result?.token
                        Log.d("LoginViewModel", "Token: $token")
                    } else {
                        Log.e("LoginViewModel", "Failed to retrieve token")
                    }
                }
            } else {
                _error.value = task.exception?.localizedMessage
            }
        }
    }

    fun saveUserToFirestore(user: FirebaseUser) {
        val userData = hashMapOf(
            "uid" to user.uid,
            "name" to (user.displayName ?: "Unknown"),
            "email" to (user.email ?: "Unknown"),
            "photoUrl" to (user.photoUrl?.toString() ?: "")
        )
        firestore.collection("users").document(user.uid)
            .set(userData)
            .addOnSuccessListener {
                _loginResult.value = user
            }
            .addOnFailureListener { e ->
                _error.value = e.localizedMessage
            }
    }

    fun clearError() {
        _error.value = null
    }
}
