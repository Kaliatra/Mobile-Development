package com.dicoding.kaliatra.ui.scan

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.dicoding.kaliatra.R
import com.dicoding.kaliatra.databinding.FragmentScanBinding
import com.dicoding.kaliatra.ui.result.ResultActivity

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scanViewModel = ViewModelProvider(this)[ScanViewModel::class.java]

        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val galleryButton = binding.galleryButton
        val cameraButton = binding.cameraButton
        val analyzeButton = binding.analyzeButton

        galleryButton.setOnClickListener {
            // Tambahkan aksi untuk tombol Galeri
        }

        cameraButton.setOnClickListener {
            // Tambahkan aksi untuk tombol Kamera
        }

        analyzeButton.setOnClickListener {
            // Tambahkan aksi untuk tombol Analisis
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_history -> {
                        val intent = Intent(requireContext(), ResultActivity::class.java)
                        startActivity(intent)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}