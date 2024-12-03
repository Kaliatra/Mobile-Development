package com.dicoding.kaliatra.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import com.dicoding.kaliatra.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var drawingView: DrawingView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        drawingView = binding.drawingView

        val buttonEraser: AppCompatImageButton = binding.buttonEraser
        buttonEraser.setOnClickListener {
            drawingView.activateEraser()
            binding.eraserSizeSeekBar.visibility = View.VISIBLE
        }

        val buttonPen: AppCompatImageButton = binding.buttonBrush
        buttonPen.setOnClickListener {
            drawingView.switchToPenTool()
            binding.eraserSizeSeekBar.visibility = View.GONE
        }


        val buttonClear: AppCompatImageButton = binding.buttonClear
        buttonClear.setOnClickListener {
            drawingView.closeDrawing()
        }

        val eraserSizeSeekBar: SeekBar = binding.eraserSizeSeekBar
        eraserSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                drawingView.setEraserSize(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}