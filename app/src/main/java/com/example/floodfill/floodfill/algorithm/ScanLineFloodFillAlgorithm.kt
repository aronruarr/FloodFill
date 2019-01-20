package com.example.floodfill.floodfill.algorithm

import android.graphics.Bitmap
import android.graphics.Point
import java.util.*

class ScanLineFloodFillAlgorithm : IFloodFillAlgorithm {

    override fun floodFill(bitmap: Bitmap, point: Point, fillColor: Int, listener: IProgressListener) {
        listener.onPrepare()

        val color = bitmap.getPixel(point.x, point.y)

        if (color == fillColor) {
            listener.onComplete()
            return
        }

        val width = bitmap.width
        val height = bitmap.height

        val queue = LinkedList<Point>().apply { add(point) }

        while (queue.isNotEmpty()) {
            val node = queue.poll()

            var x = node.x
            val y = node.y

            while ((x > 0) && (bitmap.getPixel(x - 1, y) == color)) {
                x--
            }

            var up = false
            var down = false

            while ((x < width) && (bitmap.getPixel(x, y) == color)) {
                bitmap.setPixel(x, y, fillColor)

                if (!up && (y > 0) && (bitmap.getPixel(x, y - 1) == color)) {
                    queue.add(Point(x, y - 1))
                    up = true
                } else if (up && (y > 0) && (bitmap.getPixel(x, y - 1) != color)) {
                    up = false
                }

                if (!down && (y < (height - 1)) && (bitmap.getPixel(x, y + 1) == color)) {
                    queue.add(Point(x, y + 1))
                    down = true
                } else if (down && (y < (height - 1)) && (bitmap.getPixel(x, y + 1) != color)) {
                    down = false
                }

                x++

                listener.onProgress()
            }
        }

        listener.onComplete()
    }

}