package com.dicoding.kaliatra.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.dicoding.kaliatra.R
import com.dicoding.kaliatra.databinding.FragmentHomeBinding
import com.dicoding.kaliatra.ui.result.ResultActivity

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

        val buttonEraser: Button = binding.buttoneraser
        buttonEraser.setOnClickListener {
            if (drawingView.isErasing) {
                drawingView.deactivateEraser()
                binding.eraserSizeSeekBar.visibility = View.GONE
            } else {
                drawingView.activateEraser() // Activate eraser
                binding.eraserSizeSeekBar.visibility = View.VISIBLE
            }
        }

        val buttonClear: Button = binding.buttonX
        buttonClear.setOnClickListener {
            drawingView.closeDrawing()
        }

        val buttonColor: Button = binding.buttonColor
        buttonColor.setOnClickListener {
            drawingView.setColor(getRandomColor())
        }

        val eraserSizeSeekBar: SeekBar = binding.eraserSizeSeekBar
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

    private fun getRandomColor(): Int {
        val colors = arrayOf(
            android.graphics.Color.RED,
            android.graphics.Color.BLACK,
            android.graphics.Color.GREEN,
            android.graphics.Color.BLUE,
            android.graphics.Color.YELLOW
        )
        return colors.random()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
