package com.example.floodfill.floodfill.algorithm

import android.graphics.Bitmap
import android.graphics.Point
import java.util.*

class EightDirectionsFloodFillAlgorithm : IFloodFillAlgorithm {

    private val row = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    private val col = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)

    override fun floodFill(bitmap: Bitmap, point: Point, fillColor: Int, listener: IProgressListener) {
        listener.onPrepare()

        val queue = LinkedList<Point>().apply { offer(point) }

        while (queue.isNotEmpty()) {
            val node = queue.poll()
            val color = bitmap.getPixel(node.x, node.y)

            if (fillColor == color) {
                continue
            }

            bitmap.setPixel(node.x, node.y, fillColor)

            for (i in row.indices) {
                if (isValid(bitmap, node.x + row[i], node.y + col[i], fillColor)) {
                    queue.offer(Point(node.x + row[i], node.y + col[i]))
                }
            }

            listener.onProgress()
        }

        listener.onComplete()
    }

}