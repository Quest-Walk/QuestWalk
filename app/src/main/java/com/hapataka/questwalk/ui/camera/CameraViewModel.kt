package com.hapataka.questwalk.ui.camera

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
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
import java.io.File
import javax.inject.Inject
import info.debatty.java.stringsimilarity.*

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

    lateinit var file: File

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
        file = repository.saveBitmap(bitmap.value!!, "postImage.jpg")
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
        val similarityObj = RatcliffObershelp()
        resultList.forEach { line: Line ->
            val word = line.Words[0].WordText
            Log.d("result", (similarityObj.similarity(word, keyword)).toString())
            if (word.contains(keyword)) return true
            else if (similarityObj.similarity(word, keyword) >= 0.6) return true
        }

        return false
    }

    fun failedImageDrawWithCanvas() {
        val tempBitmap = _bitmap.value ?: return
        val canvas = Canvas(tempBitmap)
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        //Left":155.0,"Top":516.0,"Height":153.0,"Width":464.0
        resultList.forEach { line ->
            canvas.drawRect(
                line.Words[0].Left.toFloat(),
                line.Words[0].Top.toFloat(),
                line.Words[0].Left + line.Words[0].Width.toFloat(),
                line.Words[0].Top + line.Words[0].Height.toFloat(), paint
            )
        }
        _bitmap.value = tempBitmap
        initIsSucceed()
    }

    fun initIsSucceed() {
        _isSucceed.value = null
    }
}