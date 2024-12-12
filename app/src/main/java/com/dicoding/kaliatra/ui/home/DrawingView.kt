package com.dicoding.kaliatra.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.dicoding.kaliatra.R

data class PathData(val path: Path, val paint: Paint)

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        strokeWidth = 40f
        style = Paint.Style.STROKE
    }

    private val path = Path()
    private var isErasing = false
    private val paths = mutableListOf<PathData>()
    private var eraserSize = 50f
    private var eraserRect: RectF = RectF()
    private var isDrawingViewActive = false

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
            val eraserPaint = Paint()
            eraserPaint.color = Color.WHITE
            eraserPaint.alpha = 100
            canvas.drawRect(eraserRect, eraserPaint)
        }

        if (!isErasing) {
            canvas.drawPath(path, paint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isDrawingViewActive) {
            return false
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
                    paths.add(PathData(Path(path), Paint(paint)))
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
    }

    private fun eraseAreaAt(x: Float, y: Float) {
        eraserRect.set(
            x - eraserSize / 2,
            y - eraserSize / 2,
            x + eraserSize / 2,
            y + eraserSize / 2
        )

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

    fun setDrawingViewActive(isActive: Boolean) {
        isDrawingViewActive = isActive
    }

    fun getDrawingBitmap(): Bitmap? {
        if (width <= 0 || height <= 0) {
            return null
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    fun hasDrawing(): Boolean {
        return paths.isNotEmpty()
    }

    fun clearDrawing() {
        paths.clear()
        invalidate()
    }
}