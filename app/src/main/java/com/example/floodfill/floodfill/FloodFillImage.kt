package com.example.floodfill.floodfill

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.util.*

class FloodFillImage {

    companion object {

        @JvmStatic
        fun newPaint() = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        @JvmStatic
        fun newBitmap(width: Int, height: Int): Bitmap {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val paint = Paint().apply {
                strokeWidth = 5f
                color = Color.WHITE
                isAntiAlias = true
            }

            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

            val random = Random()
            val size = 25
            var i = 0
            var j = 0

            while (i < canvas.width) {
                while (j < canvas.height) {
                    paint.color = if (random.nextBoolean()) Color.BLACK else Color.WHITE
                    canvas.drawRect(i.toFloat(), j.toFloat(), (i + size).toFloat(), (j + size).toFloat(), paint)

                    j += size
                }

                i += size
                j = 0
            }

            return bitmap
        }

    }

}