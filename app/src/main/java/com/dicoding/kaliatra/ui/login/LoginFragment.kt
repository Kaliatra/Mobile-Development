package com.dicoding.kaliatra.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dicoding.kaliatra.R

class LoginFragment : Fragment() {

    private lateinit var btnLogin: Button

    private val dummyEmail = "user@example.com"
    private val dummyPassword = "password123"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_login, container, false)

        btnLogin = binding.findViewById(R.id.loginButton)

        btnLogin.setOnClickListener {
            loginWithGoogle()
        }

        return binding
    }

    private fun loginWithGoogle() {
        val isLoginSuccessful = checkLogin(dummyEmail, dummyPassword)

        if (isLoginSuccessful) {
            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_onBoardingFragmentLogin_to_navigationHome)
        } else {
            Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLogin(email: String, password: String): Boolean {
        return email == dummyEmail && password == dummyPassword
    }
}
