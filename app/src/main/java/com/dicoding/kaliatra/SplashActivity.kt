package com.dicoding.kaliatra

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            if (auth.currentUser != null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("startDestination", R.id.navigation_home)
                startActivity(intent)
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("startDestination", R.id.onBoardingFragment1)
                startActivity(intent)
            }
            finish()
        }, 3000)
    }
}
