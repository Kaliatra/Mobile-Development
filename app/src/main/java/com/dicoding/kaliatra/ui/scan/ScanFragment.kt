package com.dicoding.kaliatra.ui.scan

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.dicoding.kaliatra.R
import com.dicoding.kaliatra.databinding.FragmentScanBinding
import com.dicoding.kaliatra.ui.history.HistoryActivity
import com.dicoding.kaliatra.ui.result.ResultActivity
import com.yalantis.ucrop.UCrop
import java.io.File

class ScanFragment : Fragment() {
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ScanViewModel
    private var imageUri: Uri? = null
    private lateinit var loadingDialog: Dialog

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var cropLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ScanViewModel::class.java]
        _binding = FragmentScanBinding.inflate(inflater, container, false)

        setupLoadingDialog()
        setupActivityResultLaunchers()

        binding.galleryButton.setOnClickListener { openGallery() }
        binding.cameraButton.setOnClickListener { openCamera() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_history -> {
                        val intent = Intent(requireContext(), HistoryActivity::class.java)
                        startActivity(intent)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    private fun setupLoadingDialog() {
        loadingDialog = Dialog(requireContext()).apply {
            setContentView(R.layout.custom_loading)
            setCancelable(false)
        }
    }

    private fun setupActivityResultLaunchers() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    imageUri = result.data?.data
                    imageUri?.let { startCrop(it) }
                }
            }

        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    imageUri?.let { startCrop(it) }
                }
            }

        cropLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val resultUri = UCrop.getOutput(result.data!!)
                    resultUri?.let {
                        imageUri = it
                        binding.previewImageView.setImageURI(null)
                        binding.previewImageView.setImageURI(imageUri)
                    }
                } else if (result.resultCode == UCrop.RESULT_ERROR) {
                    val cropError = UCrop.getError(result.data!!)
                    Log.e("ScanFragment", "Crop error: $cropError")
                }
            }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private fun openCamera() {
        val tempFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            tempFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }
        cameraLauncher.launch(intent)
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image.jpg"))
        val intent = UCrop.of(uri, destinationUri)
            .withOptions(UCrop.Options())
            .getIntent(requireContext())
        cropLauncher.launch(intent)
    }

    private fun analyzeImage() {
        if (imageUri != null) {
            loadingDialog.show()
            viewModel.analyzeImage(requireContext(), imageUri!!) {
                loadingDialog.dismiss()
                val intent = Intent(requireContext(), ResultActivity::class.java)
                startActivity(intent)
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.select_image_first),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
