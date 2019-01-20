package com.example.floodfill.floodfill.algorithm

import android.graphics.Bitmap
import android.graphics.Point
import androidx.core.graphics.set
import java.util.*

class ForestFireFloodFillAlgorithm : IFloodFillAlgorithm {

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

            if (isValid(bitmap, node.x + 1, node.y, fillColor)) {
                queue.offer(Point(node.x + 1, node.y))
            }

            if (isValid(bitmap, node.x - 1, node.y, fillColor)) {
                queue.offer(Point(node.x - 1, node.y))
            }

            if (isValid(bitmap, node.x, node.y + 1, fillColor)) {
                queue.offer(Point(node.x, node.y + 1))
            }

            if (isValid(bitmap, node.x, node.y - 1, fillColor)) {
                queue.offer(Point(node.x, node.y - 1))
            }

            listener.onProgress()
        }

        listener.onComplete()
    }

}