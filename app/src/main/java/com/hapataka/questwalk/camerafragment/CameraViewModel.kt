package com.hapataka.questwalk.camerafragment

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Size
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hapataka.questwalk.model.reponseocr.ResponseOcr
import com.hapataka.questwalk.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

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
        val postBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        _bitmap.postValue(postBitmap)
    }

    fun setCameraCharacteristics(rotate: Float, sizes: Array<Size>) {
        rotation = rotate
        sizeList = sizes
    }

    fun getCameraMaxSize() = sizeList.last()

    private suspend fun postCapturedImage() {
        //TODO : Bitmap -> 내부 저장소에 file 형식 으로 저장 해야함
        val file = File("")
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        RetrofitInstance.ocrSpaceApi.getImageOcrResponse(image = imagePart).enqueue(object:
            Callback<ResponseOcr> {
            override fun onResponse(call: Call<ResponseOcr>, response: Response<ResponseOcr>) {
                //응답 받으면 처리
            }

            override fun onFailure(call: Call<ResponseOcr>, t: Throwable) {
                //응답 실패시
            }
        })
    }
}