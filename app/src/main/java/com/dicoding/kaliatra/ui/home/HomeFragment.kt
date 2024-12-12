package com.dicoding.kaliatra.ui.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.dicoding.kaliatra.R
import com.dicoding.kaliatra.databinding.FragmentHomeBinding
import com.dicoding.kaliatra.ui.history.HistoryActivity
import com.dicoding.kaliatra.ui.result.ResultActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var drawingView: DrawingView
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel.resetHandwritingResponse()

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding?.root ?: return null

        drawingView = binding?.drawingView ?: return root
        drawingView.setDrawingViewActive(true)

        setupLoadingDialog()

        val buttonEraser: AppCompatImageButton = binding?.buttonEraser ?: return root
        buttonEraser.setOnClickListener {
            drawingView.activateEraser()
            binding?.eraserSizeSeekBar?.visibility = View.VISIBLE
        }

        val buttonPen: AppCompatImageButton = binding?.buttonBrush ?: return root
        buttonPen.setOnClickListener {
            drawingView.switchToPenTool()
            binding?.eraserSizeSeekBar?.visibility = View.GONE
        }

        val buttonClear: AppCompatImageButton = binding?.buttonClear ?: return root
        buttonClear.setOnClickListener { drawingView.closeDrawing() }

        val eraserSizeSeekBar: SeekBar = binding?.eraserSizeSeekBar ?: return root
        eraserSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                drawingView.setEraserSize(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

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

        suspend fun getFirebaseToken(): String? {
            val user = FirebaseAuth.getInstance().currentUser
            val tokenResult = user?.getIdToken(false)?.await()
            val token = tokenResult?.token
            return token
        }

        binding?.analyzeButton?.setOnClickListener {
            if (!drawingView.hasDrawing()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_drawing_found),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            loadingDialog.show()

            val drawingBitmap =
                binding?.drawingView?.getDrawingBitmap() ?: return@setOnClickListener
            val file = File(requireContext().getExternalFilesDir(null), "handwriting.jpg").apply {
                outputStream().use { drawingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
            }

            if (file.exists() && file.length() > 0) {
                lifecycleScope.launch {
                    val token = getFirebaseToken()
                    if (token != null) {
                        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val image =
                            MultipartBody.Part.createFormData("image", file.name, requestBody)
                        try {
                            homeViewModel.uploadHandwriting("Bearer $token", image)
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.toast_error_uploading_file),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.toast_token_not_found),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_file_creation_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        homeViewModel.errorStatus.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        homeViewModel.handwritingResponse.observe(viewLifecycleOwner) { response ->
            if (response == null) {
                loadingDialog.dismiss()
                return@observe
            }
            if (response.status == "success") {
                val uniqueFileName = "${System.currentTimeMillis()}_handwriting.jpg"
                val uploadedImageFile =
                    File(requireContext().getExternalFilesDir(null), uniqueFileName)
                val drawingBitmap = binding?.drawingView?.getDrawingBitmap()
                if (drawingBitmap != null) {
                    uploadedImageFile.outputStream()
                        .use { drawingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                }
                val intent = Intent(requireContext(), ResultActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_error_message, response.message),
                    Toast.LENGTH_SHORT
                ).show()
            }
            loadingDialog.dismiss()
        }
        return root
    }

    private fun setupLoadingDialog() {
        loadingDialog = Dialog(requireContext()).apply {
            setContentView(R.layout.custom_loading)
            setCancelable(false)
        }
    }

    override fun onResume() {
        super.onResume()
        drawingView.clearDrawing()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.resetHandwritingResponse()
        drawingView.clearDrawing()
        binding = null
    }
}