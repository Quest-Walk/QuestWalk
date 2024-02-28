package com.hapataka.questwalk.camerafragment

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {
    private var _bitmap : MutableLiveData<Bitmap> = MutableLiveData()
    val bitmap :LiveData<Bitmap> get() = _bitmap

    fun setBitmap(bitmap: Bitmap){
        _bitmap.postValue(bitmap)
    }
}