package com.hapataka.questwalk.ui.camera


import android.graphics.Bitmap

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class CameraViewModel : ViewModel() {
    private var _bitmap: MutableLiveData<Bitmap?> = MutableLiveData()
//    val bitmap: LiveData<Bitmap?> get() = _bitmap

    private var croppedBitmap: Bitmap? = null
    private var drawBoxOnBitmap: Bitmap? = null
    private var croppedSize = 0
    private var x = 0
    private var y = 0

    fun calculateAcc(appWidth: Int, appHeight: Int, inputImage: ImageProxy,sizeRate : Double) {
        val imageWidth = inputImage.height
        val imageHeight = inputImage.width
        val ratio = appHeight / imageHeight.toDouble()

        croppedSize = ((appWidth * sizeRate/2)/ratio).toInt()

        x = (imageWidth/2.0- croppedSize).toInt()
        y = (imageHeight/2.0 - croppedSize).toInt()

        return
    }

    fun imageProxyToBitmap(image: ImageProxy) {

        var bitmap = image.toBitmap()
        val rotation = image.imageInfo.rotationDegrees.toFloat()
        bitmap = rotateBitmap(bitmap, rotation)!!
        setBitmap(bitmap)

    }

    private fun setBitmap(bitmap: Bitmap?) {
        if (bitmap == null) return
        _bitmap.value = bitmap
        croppedBitmap = cropBitmap(bitmap)
    }


    private fun cropBitmap(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) return null

        return Bitmap.createBitmap(bitmap, x, y, croppedSize*2, croppedSize*2)
    }
    private fun rotateBitmap(bitmap: Bitmap?, rotation: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(rotation)
        val postBitmap =
            bitmap?.let { Bitmap.createBitmap(it, 0, 0, bitmap.width, bitmap.height, matrix, true) }
        return postBitmap
    }

    fun getCroppedBitmap() = croppedBitmap
}