package com.example.floodfill.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.floodfill.floodfill.FloodFill
import com.example.floodfill.floodfill.FloodFillImage
import com.example.floodfill.floodfill.algorithm.IProgressListener
import com.example.floodfill.viewmodel.FloodFillViewModel
import kotlinx.coroutines.Job

class FloodFillView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val bitmapRect = Rect()

    private val viewRect = Rect()

    private val paint = FloodFillImage.newPaint()

    private val touchPoint = Point()

    private val touchScale = PointF(1f, 1f)

    @Volatile
    private var isUpdating = false

    private var bitmap: Bitmap? = null

    private var job: Job? = null

    lateinit var viewModel: FloodFillViewModel

    var floodFillAlgorithm = FloodFill.Algorithm.SCAN_LINE
        set(value) {
            field = value
            cancelProgress()
        }

    @Volatile
    var updateIntervalMs = 0L
        set(value) {
            field = value
            handler.removeCallbacks(progressRunnable)
        }

    fun setImageBitmap(value: Bitmap?) {
        cancelProgress()

        bitmap = if (value != null) {
            value.copy(value.config, true)
        } else {
            value
        }

        requestLayout()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelProgress()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        var desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        bitmap?.apply {
            desiredWidth += width
            desiredHeight += height

            desiredWidth = resolveSize(desiredWidth, widthMeasureSpec)
            desiredHeight = resolveSize(desiredHeight, heightMeasureSpec)

            bitmapRect.set(0, 0, width, height)
            viewRect.set(paddingLeft, paddingTop, desiredWidth - paddingRight, desiredHeight - paddingBottom)

            if ((viewRect.width() > 0) && (viewRect.height() > 0)) {
                touchScale.x = bitmapRect.width() / viewRect.width().toFloat()
                touchScale.y = bitmapRect.height() / viewRect.height().toFloat()
            }
        }

        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.apply {
            canvas.drawBitmap(this, bitmapRect, viewRect, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if ((event.action == MotionEvent.ACTION_DOWN) && !isUpdating) {
            isUpdating = true

            touchPoint.x = (touchScale.x * event.x).toInt()
            touchPoint.y = (touchScale.y * event.y).toInt()

            bitmap?.apply {
                job = viewModel.floodFill(this, touchPoint, floodFillAlgorithm, progressListener)
            }
        }

        return true
    }

    private val progressListener = object : IProgressListener {

        @Volatile
        private var notificationTimeMs = 0L

        override fun onPrepare() {
            notificationTimeMs = System.currentTimeMillis() + updateIntervalMs
        }

        override fun onProgress() {
            val now = System.currentTimeMillis()

            if (now > notificationTimeMs) {
                notificationTimeMs = now + updateIntervalMs

                post(progressRunnable)
            }
        }

        override fun onComplete() {
            post(progressRunnable)
            isUpdating = false
        }

    }

    private fun cancelProgress() {
        isUpdating = false
        job?.apply { if (!isCancelled) cancel() }
        removeCallbacks(progressRunnable)
    }

    private val progressRunnable = Runnable { invalidate() }

}