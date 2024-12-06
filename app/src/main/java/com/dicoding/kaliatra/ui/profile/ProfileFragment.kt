package com.dicoding.kaliatra.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dicoding.kaliatra.R
import com.dicoding.kaliatra.databinding.FragmentProfileBinding

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
                findNavController().navigate(R.id.loginFragment)
            }
        }

        binding.logOut.setOnClickListener {
            profileViewModel.signOut()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}