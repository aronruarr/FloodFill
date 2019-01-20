package com.example.floodfill.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.floodfill.floodfill.FloodFill
import com.example.floodfill.floodfill.FloodFillImage
import com.example.floodfill.floodfill.algorithm.IProgressListener
import kotlinx.coroutines.launch
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class FloodFillViewModel : BaseViewModel() {

    private val bitmap = MutableLiveData<Bitmap>()

    val image: LiveData<Bitmap> = bitmap

    val isLoading = MutableLiveData<Boolean>()

    var imageWidth by ImageSize(DEFAULT_IMAGE_WIDTH_PX, MAX_IMAGE_WIDTH_PX)
    var imageHeight by ImageSize(DEFAULT_IMAGE_HEIGHT_PX, MAX_IMAGE_HEIGHT_PX)

    var floodFillAlgorithm: FloodFill.Algorithm = FloodFill.Algorithm.SCAN_LINE

    override fun onCleared() {
        super.onCleared()
        clear()
    }

    @MainThread
    fun getImage(generate: Boolean = true) {
        if (true == isLoading.value) {
            return
        }

        isLoading.value = true
        val needToGenerateImage = if (generate) true else (null == bitmap.value)

        if (!needToGenerateImage) {
            bitmap.value = bitmap.value
            isLoading.value = false
            return
        }

        clear()

        launch {
            try {
                bitmap.postValue(FloodFillImage.newBitmap(imageWidth, imageHeight))
            } catch (e: Exception) {
                // Nothing here. But it can be something useful. For example, display an error and etc.
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun floodFill(
        bitmap: Bitmap,
        point: Point,
        type: FloodFill.Algorithm,
        progressListener: IProgressListener
    ) = launch {
        try {
            val algorithm = FloodFill.from(type)
            algorithm.floodFill(bitmap, point, Color.BLACK, progressListener)
        } catch (e: Exception) {
            // Nothing here. But it can be something useful. For example, display an error and etc.
        }
    }

    fun isValidImageWidth(value: Int) = (value > 0) && (value < MAX_IMAGE_WIDTH_PX)

    fun isValidImageHeight(value: Int) = (value > 0) && (value < MAX_IMAGE_HEIGHT_PX)

    private fun clear() {
        bitmap.value?.apply {
            if (!isRecycled) recycle()
        }
    }

    companion object {
        const val DEFAULT_IMAGE_WIDTH_PX = 400
        const val DEFAULT_IMAGE_HEIGHT_PX = 400

        const val MAX_IMAGE_WIDTH_PX = 1024
        const val MAX_IMAGE_HEIGHT_PX = 1024
    }

    class ImageSize(var value: Int, private val maxValue: Int) : ReadWriteProperty<Any, Int> {

        override fun getValue(thisRef: Any, property: KProperty<*>): Int {
            return value
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
            if (value < 0) throw IllegalArgumentException("Image dimension must be non-negative value. But it's $value")
            if (value > maxValue) throw IllegalArgumentException("The value: $value of image dimension is bigger than max value: $maxValue")
            this.value = value
        }

    }

}