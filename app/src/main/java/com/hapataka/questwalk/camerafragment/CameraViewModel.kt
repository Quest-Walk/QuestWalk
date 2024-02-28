package com.hapataka.questwalk.camerafragment

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {
    private var _bitmap : MutableLiveData<Bitmap> = MutableLiveData()
    val bitmap :LiveData<Bitmap> get() = _bitmap

    fun setBitmap(bitmap: Bitmap){
        //전처리 과정을 마치고 포스트??
        _bitmap.postValue(bitmap)
    }
}