package com.hapataka.questwalk.camerafragment

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Size
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {
    private var _bitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val bitmap: LiveData<Bitmap> get() = _bitmap


    // 카메라 Hardware 정보
    private var rotation: Float = 0F
    private lateinit var sizeList: Array<Size>
    fun setBitmap(bitmap: Bitmap) {
        //전처리 과정을 마치고 포스트??

        val matrix = Matrix()
        matrix.postRotate(rotation)
        val postBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        _bitmap.postValue(postBitmap)
    }

    fun setCameraCharacteristics(rotate: Float,sizes : Array<Size>){
        rotation = rotate
        sizeList = sizes
    }
    fun getCameraMaxSize() = sizeList.last()
}