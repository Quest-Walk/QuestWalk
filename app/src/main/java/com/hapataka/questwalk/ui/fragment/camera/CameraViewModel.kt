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
    val bitmap: LiveData<Bitmap?> get() = _bitmap

    private var croppedBitmap: Bitmap? = null
    private var drawBoxOnBitmap: Bitmap? = null




    private var croppedSize = 0
    private var x = 0
    private var y = 0
    fun calculateAcc(appWidth: Int, appHeight: Int, inputImage: ImageProxy,sizeRate : Double) {
        val imageWidth = inputImage.height // 1392
        val imageHeight = inputImage.width // 1856
        //1. getRatio 세로 길이가 더 긴 상황 이므로
        val ratio = appHeight / imageHeight.toDouble()

        //3.getCropWidth
        croppedSize = ((appWidth * sizeRate/2)/ratio).toInt()

        //4.getX
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
        drawBoxOnBitmap = drawBoxOnBitmap(bitmap)
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

    private fun drawBoxOnBitmap(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) return null
        val drawBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(drawBitmap)
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        canvas.drawRect(x.toFloat(), y.toFloat(), x + 2*croppedSize.toFloat(), y + 2*croppedSize.toFloat(), paint)
        return drawBitmap
    }



    fun getCroppedBitmap() = croppedBitmap
}