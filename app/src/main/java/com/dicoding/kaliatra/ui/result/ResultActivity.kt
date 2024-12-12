package com.dicoding.kaliatra.ui.result

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.kaliatra.R
import com.dicoding.kaliatra.databinding.ActivityResultBinding
import com.dicoding.kaliatra.utils.LocaleHelper
import com.dicoding.kaliatra.utils.PreferenceManager
import com.google.firebase.auth.FirebaseAuth

class ResultActivity : AppCompatActivity() {

    private val resultViewModel: ResultViewModel by viewModels()
    private lateinit var binding: ActivityResultBinding

    override fun attachBaseContext(newBase: Context) {
        val savedLanguage = PreferenceManager.getSavedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.setLocale(newBase, savedLanguage))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.result_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val shimmerImage = binding.shimmerImage
        val shimmerText = binding.shimmerText

        shimmerImage.startShimmer()
        shimmerText.startShimmer()

        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    token?.let {
                        resultViewModel.getLatestPrediction(it)
                    } ?: run {
                        Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Gagal mendapatkan token", Toast.LENGTH_SHORT).show()
                }
            }

        resultViewModel.lastPrediction.observe(this) { response ->
            response?.let {
                shimmerImage.stopShimmer()
                shimmerText.stopShimmer()

                shimmerImage.visibility = View.GONE
                shimmerText.visibility = View.GONE

                binding.resultImage.visibility = View.VISIBLE
                binding.resultText.visibility = View.VISIBLE

                val predictions = response.data.topPredictions.joinToString(", ") { it }
                binding.resultText.text = getString(R.string.result_text, predictions)

                Glide.with(this)
                    .load(it.data.imageURL)
                    .into(binding.resultImage)
            }
        }

        resultViewModel.errorMessage.observe(this) { error ->
            shimmerImage.stopShimmer()
            shimmerText.stopShimmer()

            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
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