package com.dicoding.kaliatra.ui.history

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.kaliatra.R
import com.dicoding.kaliatra.databinding.ActivityHistoryBinding
import com.dicoding.kaliatra.utils.LocaleHelper
import com.dicoding.kaliatra.utils.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    override fun attachBaseContext(newBase: Context) {
        val savedLanguage = PreferenceManager.getSavedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.setLocale(newBase, savedLanguage))
    }

    private val historyViewModel: HistoryViewModel by lazy {
        ViewModelProvider(
            this,
            HistoryViewModelFactory(application)
        )[HistoryViewModel::class.java]
    }

    private val historyAdapter = HistoryAdapter { id ->
        lifecycleScope.launch {
            val token = getFirebaseToken()
            if (token != null) {
                historyViewModel.deletePredictionHistory(id, "Bearer $token", this@HistoryActivity)
            } else {
                Toast.makeText(
                    this@HistoryActivity,
                    getString(R.string.token_not_found),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private suspend fun getFirebaseToken(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return try {
            val tokenResult = user?.getIdToken(false)?.await()
            tokenResult?.token
        } catch (e: Exception) {
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.history_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = historyAdapter

        observeViewModel()

        lifecycleScope.launch {
            val token = getFirebaseToken()
            if (token != null) {
                historyViewModel.getPredictionHistories("Bearer $token")
            } else {
                Toast.makeText(
                    this@HistoryActivity,
                    getString(R.string.token_not_found),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun observeViewModel() {
        historyViewModel.historyList.observe(this) { histories ->
            historyAdapter.submitList(histories)
            binding.emptyTextView.visibility =
                if (histories.isNullOrEmpty()) View.VISIBLE else View.GONE
            binding.recyclerView.visibility =
                if (histories.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        historyViewModel.loadingStatus.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        historyViewModel.deleteStatus.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}