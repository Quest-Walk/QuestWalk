package com.hapataka.questwalk.ui.camera


import android.graphics.Bitmap

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text.Element
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import info.debatty.java.stringsimilarity.RatcliffObershelp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


@HiltViewModel
class CameraViewModel @Inject constructor(private val repository: CameraRepository) : ViewModel() {
    private var _bitmap: MutableLiveData<Bitmap?> = MutableLiveData()
    val bitmap: LiveData<Bitmap?> get() = _bitmap
    private var _isSucceed: MutableLiveData<Boolean?> = MutableLiveData()
    val isSucceed: LiveData<Boolean?> get() = _isSucceed

    private var resultListByMLKit: MutableList<Element> = mutableListOf()

    var file: File? = null
    // Crop event

    var isCropped = true
    private var croppedBitmap: Bitmap? = null
    private var drawBoxOnBitmap: Bitmap? = null

    private var _isDebug: MutableLiveData<Boolean> = MutableLiveData(true)
    val isDebug: LiveData<Boolean> get() = _isDebug

    fun clickedCropImageButton() {
        isCropped = !isCropped
    }

    /**
     *  Bitmap 파일 처리 부분
     */
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

    fun initBitmap() {
        _bitmap.value = null
        file = null
        resultListByMLKit.clear()
        _isSucceed.value = null
    }

    fun deleteBitmapByFile() {
        repository.deleteBitmap()
    }

    fun getCroppedBitmap() = croppedBitmap
    fun getDrawBoxOnBitmap() = drawBoxOnBitmap


    /**
     *  Ocr 처리(google MLKit 이용)
     */

    val isLoading = MutableLiveData(false)
    fun postCapturedImageWithMLKit(keyword: String) {

        viewModelScope.launch {
            processImage(keyword)
        }
    }

    private fun preProcessImage(bitmap: Bitmap?): Bitmap?{
        var resultImage = bitmap
        resultImage = repository.toGrayScaleBitmap(resultImage)
        resultImage = repository.contractBitmap(resultImage,1.2f,-50f)
        return resultImage
    }
    private suspend fun processImage(keyword: String) = withContext(Dispatchers.IO) {
        val image: InputImage
        image = if (isCropped) {
            croppedBitmap = preProcessImage(croppedBitmap)
            InputImage.fromBitmap(croppedBitmap!!, 0)
        } else {
            InputImage.fromBitmap(bitmap.value!!, 0)
        }

        val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

        try {
            isLoading.postValue(true)

            val result = recognizer.process(image).await()

            for (block in result.textBlocks) {
                for (line in block.lines) {
                    for (element in line.elements) {
                        resultListByMLKit.add(element)
                        Log.d("ocrResult", element.text)
                    }
                }
            }
            isLoading.postValue(false)

            _isSucceed.postValue(validationResponseByMLKit(keyword))

        } catch (e: Exception) {
            Log.d("ocrResult", e.toString())
            isLoading.postValue(false)
        }
    }

    private fun validationResponseByMLKit(keyword: String): Boolean {

        var isValidated = false
        val similarityObj = RatcliffObershelp()

        resultListByMLKit.forEach { element: Element ->
            val word = element.text
            Log.d("ocrResult", word+"!")
            if (word.contains(keyword)) {
                isValidated = true
                return@forEach
            } else if (similarityObj.similarity(word, keyword) >= 0.3) {
                isValidated = true
                return@forEach
            }
            Log.d("ocrResultSimilar", similarityObj.similarity(word, keyword).toString())
        }
        if (isValidated) {
            file = repository.saveBitmap(bitmap.value!!, "resultImage.png")
        } else {
            file = null
            repository.deleteBitmap()
        }
        return isValidated
    }

    fun failedImageDrawWithCanvasByMLKit(keyword: String) {
        val tempBitmap = _bitmap.value ?: return
        val canvas = Canvas(tempBitmap)
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        val similarityObj = RatcliffObershelp()
        val keywordPaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        resultListByMLKit.forEach { element ->
            val word = element.text
            if (similarityObj.similarity(word, keyword) >= 0.2)
                canvas.drawRect(element.boundingBox!!, keywordPaint)
            else
                canvas.drawRect(element.boundingBox!!, paint)
        }

        _bitmap.value = tempBitmap
        initIsSucceed()
    }

    //초기화
    fun initIsSucceed() {
        _isSucceed.value = null
    }


    /**
     *  Debug
     */
    fun setDebug() {
        _isDebug.value = !_isDebug.value!!
    }

    fun setBitmapByGallery(bitmap: Bitmap) {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        _bitmap.value = mutableBitmap
        if(bitmap.width<=bitmap.height) {
            croppedSize = (bitmap.width * 0.4).toInt()
        }else{
            croppedSize = (bitmap.height * 0.4).toInt()
        }
        x = bitmap.width/2 - croppedSize
        y = bitmap.height/2 - croppedSize
//        if(y<0) y = -y
        croppedBitmap = cropBitmap(mutableBitmap)
        drawBoxOnBitmap = drawBoxOnBitmap(mutableBitmap)
    }

}