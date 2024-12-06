package com.dicoding.kaliatra

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dicoding.kaliatra.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            navController.navigate(R.id.onBoardingFragment1)
        } else {
            navController.navigate(R.id.navigation_home)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.onBoardingFragment1, R.id.onBoardingFragment2, R.id.onBoardingFragment3, R.id.loginFragment -> {
                    supportActionBar?.hide()
                    navView.visibility = BottomNavigationView.GONE
                }

                R.id.navigation_home -> {
                    supportActionBar?.show()
                    supportActionBar?.title = getString(R.string.home_title)
                    navView.visibility = BottomNavigationView.VISIBLE
                }

                R.id.navigation_scan -> {
                    supportActionBar?.show()
                    supportActionBar?.title = getString(R.string.scan_title)
                    navView.visibility = BottomNavigationView.VISIBLE
                }

                R.id.navigation_dictionary -> {
                    supportActionBar?.show()
                    supportActionBar?.title = getString(R.string.dictionary_title)
                    navView.visibility = BottomNavigationView.VISIBLE
                }

                R.id.navigation_profile -> {
                    supportActionBar?.show()
                    supportActionBar?.title = getString(R.string.profile_title)
                    navView.visibility = BottomNavigationView.VISIBLE
                }

                else -> {
                    supportActionBar?.hide()
                    navView.visibility = BottomNavigationView.VISIBLE
                }
            }
        }
        navView.setupWithNavController(navController)
    }
}
