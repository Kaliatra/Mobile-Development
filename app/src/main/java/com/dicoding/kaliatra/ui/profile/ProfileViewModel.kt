package com.dicoding.kaliatra.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    init {
        fetchUser()
    }

    private fun fetchUser() {
        _user.value = FirebaseAuth.getInstance().currentUser
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        _user.value = null
    }
}