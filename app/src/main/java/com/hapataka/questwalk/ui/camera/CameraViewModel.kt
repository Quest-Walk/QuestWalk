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
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.Text.Element
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@HiltViewModel
class CameraViewModel @Inject constructor(private val repository: CameraRepository) : ViewModel() {
    private var _bitmap: MutableLiveData<Bitmap?> = MutableLiveData()
    val bitmap: LiveData<Bitmap?> get() = _bitmap

    private var _isSucceed: MutableLiveData<Boolean?> = MutableLiveData()
    val isSucceed: LiveData<Boolean?> get() = _isSucceed

    private var resultListBySpaceAPI: ArrayList<Line> = arrayListOf()
    private var resultListByMLKit: MutableList<Text.Element> = mutableListOf()

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


    /**
     *  google MLKit 이용
     *
     */
    val isLoading = MutableLiveData(false)
    fun postCapturedImageWithMLKit(keyword: String) {
        viewModelScope.launch {
            processImage(keyword)
        }
    }

    private suspend fun processImage(keyword: String) = withContext(Dispatchers.IO) {
        val image = InputImage.fromBitmap(bitmap.value!!, 0)
        val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
        file = repository.saveBitmap(bitmap.value!!,"resultImage.jpg")
        try {
            isLoading.postValue(true)
            val result = recognizer.process(image).await()

            for (block in result.textBlocks) {
                for (line in block.lines) {
                    for (element in line.elements) {
                        resultListByMLKit.add(element)
                    }
                }
            }
            isLoading.postValue(false)

            _isSucceed.postValue(validationResponseByMLKit(keyword))

        } catch (e: Exception) {
            Log.d("result",e.toString())
            isLoading.postValue(false)
        }
    }

    private fun validationResponseByMLKit(keyword: String): Boolean {
        val similarityObj = RatcliffObershelp()
        resultListByMLKit.forEach { element : Element ->
            val word = element.text
            Log.d("result",word)
            if (word.contains(keyword)) return true
            else if (similarityObj.similarity(word, keyword) >= 0.6) return true
        }

        return false
    }

    fun failedImageDrawWithCanvasByMLKit(){
        val tempBitmap = _bitmap.value ?: return
        val canvas = Canvas(tempBitmap)
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        resultListByMLKit.forEach { element ->
            canvas.drawRect(element.boundingBox!!,paint)
        }


        _bitmap.value = tempBitmap
        initIsSucceed()
    }



    /**
     *  OcrSpaceAPI 이용
     */
    suspend fun postCapturedImageWithOcrSpaceAPI(keyword: String) {
        file = repository.saveBitmap(bitmap.value!!, "resultImage.jpg")


//        var postBitmap = repository.resizedBitmap(bitmap.value!!,1000)
//        postBitmap = repository.toGrayScaleBitmap(postBitmap)
//        val postFile = repository.saveBitmap(postBitmap,"postImage.jpg")

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val responseOcr = RetrofitInstance.ocrSpaceApi.getImageOcrResponse(file = imagePart)
        if (responseOcr.isSuccessful) {
            val response: OcrResponse = responseOcr.body()!!
            if (response.OCRExitCode == "6") return
            resultListBySpaceAPI = response.ParsedResults[0].TextOverlay.Lines as ArrayList<Line>
            Log.d("result", resultListBySpaceAPI.toString())
            _isSucceed.value = validationResponseByOcrSpaceAPI(keyword)
        }
    }

    private fun validationResponseByOcrSpaceAPI(keyword: String): Boolean {
        //Line 내에 Words 의 WordText 를 비교해야함
        val similarityObj = RatcliffObershelp()
        resultListBySpaceAPI.forEach { line: Line ->
            val word = line.Words[0].WordText
            Log.d("result", (similarityObj.similarity(word, keyword)).toString())
            if (word.contains(keyword)) return true
            else if (similarityObj.similarity(word, keyword) >= 0.6) return true
        }

        return false
    }

    fun failedImageDrawWithCanvasByOcrSpaceAPI() {
        val tempBitmap = _bitmap.value ?: return
        val canvas = Canvas(tempBitmap)
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        //Left":155.0,"Top":516.0,"Height":153.0,"Width":464.0
        resultListBySpaceAPI.forEach { line ->
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