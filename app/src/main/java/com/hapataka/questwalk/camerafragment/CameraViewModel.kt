package com.hapataka.questwalk.camerafragment

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.util.Size
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hapataka.questwalk.model.reponseocr.Line
import com.hapataka.questwalk.model.reponseocr.OcrResponse
import com.hapataka.questwalk.network.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val repository: CameraRepository) : ViewModel() {
    private var _bitmap: MutableLiveData<Bitmap?> = MutableLiveData()
    val bitmap: LiveData<Bitmap?> get() = _bitmap

    private var _isSucceed: MutableLiveData<Boolean?> = MutableLiveData()
    val isSucceed: LiveData<Boolean?> get() = _isSucceed

    private var resultList: ArrayList<Line> = arrayListOf()

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

    fun initBitmap() {
        _bitmap.value = null
    }

    fun setCameraCharacteristics(rotate: Float, sizes: Array<Size>) {
        rotation = rotate
        sizeList = sizes
    }

    fun getCameraMaxSize() = sizeList.last()

    suspend fun postCapturedImage(keyword: String) {
        val file = repository.saveBitmap(bitmap.value!!, "postImage.jpg")
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val responseOcr = RetrofitInstance.ocrSpaceApi.getImageOcrResponse(file = imagePart)
        if (responseOcr.isSuccessful) {
            val response: OcrResponse = responseOcr.body()!!
            resultList = response.ParsedResults[0].TextOverlay.Lines as ArrayList<Line>
            Log.d("result", resultList.toString())
            _isSucceed.value = validationResponse(keyword)
        }
    }

    private fun validationResponse(keyword: String): Boolean {
        //Line 내에 Words 의 WordText 를 비교해야함
//        var lines = resultList.filter{it.Words[0].WordText =="브라운치즈"}
        resultList.forEach { line: Line ->
            if (line.Words[0].WordText.contains(keyword)) return true
        }

        return false
    }

}