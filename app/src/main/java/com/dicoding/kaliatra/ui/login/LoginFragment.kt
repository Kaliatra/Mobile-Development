package com.dicoding.kaliatra.ui.login

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dicoding.kaliatra.R
import com.dicoding.kaliatra.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.dicoding.kaliatra.BuildConfig

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var loadingDialog: Dialog

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
    }

    private val signInLauncher = registerForActivityResult(StartIntentSenderForResult()) { result ->
        if (result.resultCode == -1) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                credential.googleIdToken?.let { idToken ->
                    viewModel.signInWithGoogle(idToken)
                } ?: Log.e("LoginFragment", "No ID Token found.")
            } catch (e: ApiException) {
                Log.e("LoginFragment", "One Tap Sign-In failed: ${e.statusCode}", e)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        setupSignInRequest()
        setupLoadingDialog()

        if (FirebaseAuth.getInstance().currentUser != null) {
            navigateToHome()
        }

        binding.loginButton.setOnClickListener { initiateGoogleSignIn() }
        observeViewModel()
        return binding.root
    }

    private fun setupSignInRequest() {
        oneTapClient = Identity.getSignInClient(requireContext())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.DEFAULT_WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    private fun setupLoadingDialog() {
        loadingDialog = Dialog(requireContext()).apply {
            setContentView(R.layout.custom_loading)
            setCancelable(false)
        }
    }

    private fun initiateGoogleSignIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    signInLauncher.launch(
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    )
                } catch (e: Exception) {
                    Log.e("LoginFragment", "Google Sign-In failed", e)
                }
            }
            .addOnFailureListener { e ->
                Log.e("LoginFragment", "Google Sign-In request failed: ${e.localizedMessage}", e)
            }
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            toggleLoadingDialog(isLoading)
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { user ->
            user?.let {
                viewModel.saveUserToFirestore(it)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.login_successful),
                    Toast.LENGTH_SHORT
                ).show()
                navigateToHome()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.navigation_home)
    }

    private fun toggleLoadingDialog(show: Boolean) {
        if (show) loadingDialog.show() else loadingDialog.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
