package com.example.floodfill.floodfill.algorithm

import android.graphics.Bitmap
import android.graphics.Point

interface IFloodFillAlgorithm {

    fun floodFill(
        bitmap: Bitmap, point: Point,
        fillColor: Int,
        listener: IProgressListener
    )

    fun isValid(bitmap: Bitmap, x: Int, y: Int, fillColor: Int) =
        (x >= 0) && (x < bitmap.width) && (y >= 0) && (y < bitmap.height)
                && (bitmap.getPixel(x, y) != fillColor)

}