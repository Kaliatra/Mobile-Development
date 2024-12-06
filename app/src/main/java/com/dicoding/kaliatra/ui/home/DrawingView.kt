package com.dicoding.kaliatra.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.dicoding.kaliatra.R

// PathData class to hold path and paint
data class PathData(val path: Path, val paint: Paint)

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        strokeWidth = 40f
        style = Paint.Style.STROKE
    }

    private val path = Path()

    private var isErasing = false

    private val paths = mutableListOf<PathData>()

    private var eraserSize = 50f
    private var eraserRect: RectF = RectF()

    private var isDrawingViewActive = false  // Flag to track if DrawingView is active

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw all the paths
        for (pathData in paths) {
            canvas.drawPath(pathData.path, pathData.paint)
        }

        // Draw eraser area (highlight the area being erased)
        if (isErasing) {
            val eraserPaint = Paint()
            eraserPaint.color = Color.GRAY
            eraserPaint.alpha = 100
            canvas.drawRect(eraserRect, eraserPaint)
        }

        // Draw the current path if not erasing
        if (!isErasing) {
            canvas.drawPath(path, paint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isDrawingViewActive) {
            return false  // If DrawingView is not active, ignore touch events
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isErasing) {
                    eraseAreaAt(event.x, event.y)
                    path.reset()
                } else {
                    path.moveTo(event.x, event.y)
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isErasing) {
                    eraseAreaAt(event.x, event.y)
                } else {
                    path.lineTo(event.x, event.y)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isErasing) {
                    paths.add(PathData(Path(path), Paint(paint)))  // Add path to paths list
                }
                path.reset()
            }
        }
        invalidate()
        return super.onTouchEvent(event)
    }

    fun activateEraser() {
        if (isDrawingViewActive) {
            isErasing = true
            showToast(R.string.eraser_active)
        }
    }

    fun switchToPenTool() {
        if (isDrawingViewActive) {
            isErasing = false
            showToast(R.string.pen_tool_active)
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(context, context.getString(messageResId), Toast.LENGTH_SHORT).show()
    }

    fun closeDrawing() {
        path.reset()
        paths.clear()
        invalidate()
    }

    fun setEraserSize(size: Float) {
        eraserSize = size
        val message = context.getString(R.string.eraser_size, size)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun eraseAreaAt(x: Float, y: Float) {
        eraserRect.set(x - eraserSize / 2, y - eraserSize / 2, x + eraserSize / 2, y + eraserSize / 2)

        for (pathData in paths) {
            val path = pathData.path

            val pathMeasure = PathMeasure(path, false)
            val pathLength = pathMeasure.length

            var segmentDistance = 0f
            val segmentStep = 5f

            while (segmentDistance < pathLength) {
                val coords = FloatArray(2)
                pathMeasure.getPosTan(segmentDistance, coords, null)

                val segmentX = coords[0]
                val segmentY = coords[1]

                if (eraserRect.contains(segmentX, segmentY)) {
                    val newPath = Path()

                    pathMeasure.getSegment(0f, segmentDistance, newPath, true)
                    pathData.path.set(newPath)
                }
                segmentDistance += segmentStep
            }
        }

        invalidate()
    }

    // New method to set the drawing view active state
    fun setDrawingViewActive(isActive: Boolean) {
        isDrawingViewActive = isActive
    }
}