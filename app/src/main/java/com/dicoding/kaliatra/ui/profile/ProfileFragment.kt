package com.dicoding.kaliatra.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dicoding.kaliatra.R
import com.dicoding.kaliatra.SplashActivity
import com.dicoding.kaliatra.databinding.FragmentProfileBinding
import com.dicoding.kaliatra.utils.LocaleHelper
import com.dicoding.kaliatra.utils.PreferenceManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.profileNamePlaceholder.text = it.displayName
                binding.profileEmailPlaceholder.text = it.email

                Glide.with(this)
                    .load(it.photoUrl)
                    .placeholder(R.drawable.ic_person_placeholder)
                    .circleCrop()
                    .into(binding.profileImagePlaceholder)
            } ?: run {
                val intent = Intent(requireContext(), SplashActivity::class.java)
                startActivity(intent)
            }
        }

        binding.changeLanguage.setOnClickListener {
            showLanguageSelectionDialog()
        }

        binding.logOut.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.aboutApp.setOnClickListener {
            findNavController().navigate(R.id.navigation_about_apps)
        }

        return root
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("Indonesia", "English")
        val languageCodes = arrayOf("id", "en")
        val currentLanguage = PreferenceManager.getSavedLanguage(requireContext())
        val selectedLanguageIndex = languageCodes.indexOf(currentLanguage)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_language))
            .setSingleChoiceItems(languages, selectedLanguageIndex) { dialog, which ->
                val selectedLanguageCode = languageCodes[which]

                updateLanguage(selectedLanguageCode)

                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    private fun updateLanguage(languageCode: String) {
        PreferenceManager.saveLanguage(requireContext(), languageCode)
        LocaleHelper.setLocale(requireContext(), languageCode)
        Toast.makeText(
            requireContext(),
            getString(
                R.string.language_changed,
                if (languageCode == "id") "Indonesia" else "English"
            ),
            Toast.LENGTH_LONG
        ).show()
        requireActivity().recreate()
    }


    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirm_logout_title))
            .setMessage(getString(R.string.confirm_logout_message))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                profileViewModel.signOut()
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}