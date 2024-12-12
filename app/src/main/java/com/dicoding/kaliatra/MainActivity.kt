package com.dicoding.kaliatra

import android.content.Context
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dicoding.kaliatra.databinding.ActivityMainBinding
import com.dicoding.kaliatra.utils.LocaleHelper
import com.dicoding.kaliatra.utils.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var currentDestinationId: Int? = null

    override fun attachBaseContext(newBase: Context) {
        val savedLanguage = PreferenceManager.getSavedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.setLocale(newBase, savedLanguage))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        if (savedInstanceState == null) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                navController.navigate(R.id.onBoardingFragment1)
            } else {
                navController.navigate(R.id.navigation_home)
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestinationId = destination.id
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

                R.id.navigation_about_apps -> {
                    supportActionBar?.show()
                    supportActionBar?.title = getString(R.string.about_apps_title)
                    navView.visibility = BottomNavigationView.GONE
                }

                else -> {
                    supportActionBar?.hide()
                    navView.visibility = BottomNavigationView.VISIBLE
                }
            }
        }

        navView.setOnItemReselectedListener {
        }

        navView.setOnItemSelectedListener { item ->
            if (currentDestinationId != item.itemId) {
                val navOptions = androidx.navigation.NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.navigation_home, false)
                    .build()
                navController.navigate(item.itemId, null, navOptions)
            }
            true
        }
        navView.setupWithNavController(navController)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (navController.currentDestination?.id) {
                    R.id.navigation_home,
                    R.id.loginFragment,
                    R.id.onBoardingFragment1 -> {
                        finish()
                    }

                    R.id.navigation_scan,
                    R.id.navigation_dictionary,
                    R.id.navigation_profile -> {
                        binding.navView.selectedItemId = R.id.navigation_home
                    }

                    R.id.navigation_about_apps -> {
                        navController.navigateUp()
                    }

                    else -> {
                        if (isEnabled) {
                            isEnabled = false
                            onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }
            }
        })
    }
}