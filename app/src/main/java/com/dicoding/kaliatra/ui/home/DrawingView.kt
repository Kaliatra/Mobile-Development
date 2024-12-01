package com.dicoding.kaliatra.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.widget.Toast
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.*
import kotlin.math.pow
import kotlin.math.sqrt

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }

    private val path = Path()

    internal var isErasing = false

    private val paths = mutableListOf<PathData>()

    private var eraserSize = 50f

    private var eraserX = 0f
    private var eraserY = 0f

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (pathData in paths) {
            canvas.drawPath(pathData.path, pathData.paint)
        }

        if (isErasing) {
            val eraserPath = Path()
            eraserPath.addCircle(eraserX, eraserY, eraserSize / 2, Path.Direction.CW)
            canvas.drawPath(eraserPath, paint.apply { color = Color.GRAY })
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                if (isErasing) {
                    erasePathAtAsync(event.x, event.y)
                }
                eraserX = event.x
                eraserY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isErasing) {
                    erasePathAtAsync(event.x, event.y)
                } else {
                    path.lineTo(event.x, event.y)
                }
                eraserX = event.x
                eraserY = event.y
            }
            MotionEvent.ACTION_UP -> {
                if (!isErasing) {
                    paths.add(PathData(Path(path), Paint(paint)))
                }
                path.reset()
            }
        }
        invalidate()
        return super.onTouchEvent(event)
    }

    fun activateEraser() {
        isErasing = true
        Toast.makeText(context, "Eraser Activated", Toast.LENGTH_SHORT).show()
    }

    fun deactivateEraser() {
        isErasing = false
        Toast.makeText(context, "Eraser Deactivated", Toast.LENGTH_SHORT).show()
    }

    fun setColor(color: Int) {
        if (!isErasing) {
            paint.color = color
            Toast.makeText(context, "Color changed to: ${getColorName(color)}", Toast.LENGTH_SHORT).show()
        }
    }

    fun closeDrawing() {
        path.reset()
        paths.clear()
        invalidate()
    }

    private fun getColorName(color: Int): String {
        return when (color) {
            Color.BLACK -> "Black"
            Color.RED -> "Red"
            Color.GREEN -> "Green"
            Color.BLUE -> "Blue"
            Color.YELLOW -> "Yellow"
            else -> "Custom Color"
        }
    }

    private fun erasePathAtAsync(x: Float, y: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            erasePathAt(x, y)
            withContext(Dispatchers.Main) {
                invalidate()
            }
        }
    }

    private fun erasePathAt(x: Float, y: Float) {
        val iterator = paths.iterator()
        while (iterator.hasNext()) {
            val pathData = iterator.next()
            val path = pathData.path

            val pathMeasure = android.graphics.PathMeasure(path, false)
            val distance = FloatArray(2)

            var segmentDistance = 0f
            while (pathMeasure.getPosTan(segmentDistance, distance, null)) {
                val segmentX = distance[0]
                val segmentY = distance[1]

                val distanceToPath = sqrt((x - segmentX).toDouble().pow(2) + (y - segmentY).toDouble().pow(2))
                if (distanceToPath <= eraserSize) {
                    iterator.remove()
                    return
                }
                segmentDistance += 5f
            }
        }
    }

    fun setEraserSize(size: Float) {
        eraserSize = size
        Toast.makeText(context, "Eraser size changed to: $size", Toast.LENGTH_SHORT).show()
    }

    data class PathData(val path: Path, val paint: Paint)
}
