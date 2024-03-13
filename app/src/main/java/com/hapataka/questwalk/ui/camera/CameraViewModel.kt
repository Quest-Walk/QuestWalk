package com.hapataka.questwalk.ui.camera


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import androidx.camera.core.ImageCapture
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

    private var resultListByMLKit: MutableList<Element> = mutableListOf()

    // 카메라 Hardware 정보
    private var flashMode = ImageCapture.FLASH_MODE_OFF

    // 해당 imageFile 경로
    var file: File? = null

    // Crop event

    var isCropped = false
    private var croppedBitmap: Bitmap? = null
    private var drawBoxOnBitmap: Bitmap? = null

    private var _isDebug: MutableLiveData<Boolean> = MutableLiveData(true)
    val isDebug: LiveData<Boolean> get() = _isDebug

    fun clickedCropImageButton() {
        isCropped = !isCropped
    }

    /**
     *  Camera 처리 부분
     */
    fun toggleFlash(): Int {
        flashMode = if (flashMode == ImageCapture.FLASH_MODE_OFF) {
            ImageCapture.FLASH_MODE_ON
        } else {
            ImageCapture.FLASH_MODE_OFF
        }
        return flashMode
    }


    /**
     *  Bitmap 파일 처리 부분
     */

    fun imageProxyToBitmap(image: ImageProxy) {

        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        var bitmap: Bitmap? = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val rotation = image.imageInfo.rotationDegrees.toFloat()
        bitmap = rotateBitmap(bitmap, rotation)
        setBitmap(bitmap)

    }

    private fun setBitmap(bitmap: Bitmap?) {
        if (bitmap == null) return
        _bitmap.value = bitmap
        croppedBitmap = cropBitmap(bitmap, 50)
        drawBoxOnBitmap = drawBoxOnBitmap(bitmap, 50)
    }


    private fun cropBitmap(bitmap: Bitmap?, dp: Int): Bitmap? {
        if (bitmap == null) return null
        val offsetPx = repository.dpToPx(dp).toInt()
        val x = 0
        val y = (bitmap.height / 2) - offsetPx

        val width = bitmap.width
        val height = 2 * offsetPx

        return Bitmap.createBitmap(bitmap, x, y, width, height)
    }


    private fun rotateBitmap(bitmap: Bitmap?, rotation: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(rotation)
        val postBitmap =
            bitmap?.let { Bitmap.createBitmap(it, 0, 0, bitmap.width, bitmap.height, matrix, true) }
        return postBitmap
    }

    private fun drawBoxOnBitmap(bitmap: Bitmap?, dp: Int): Bitmap? {
        if (bitmap == null) return null
        val drawBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(drawBitmap)
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        val offsetPx = repository.dpToPx(dp)

        val left = 0f
        val top = (drawBitmap.height / 2) - offsetPx
        val right = drawBitmap.width.toFloat()
        val bottom = (drawBitmap.height / 2) + offsetPx
        canvas.drawRect(left, top, right, bottom, paint)
        return drawBitmap
    }

    fun initBitmap() {
        _bitmap.value = null
        file = null
        resultListByMLKit.clear()
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

    private suspend fun processImage(keyword: String) = withContext(Dispatchers.IO) {
        val image: InputImage = if (isCropped) {
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
            Log.d("ocrResult", word)
            if (word.contains(keyword)) {
                isValidated = true
                return@forEach
            } else if (similarityObj.similarity(word, keyword) >= 0.6) {
                isValidated = true
                return@forEach
            }
            Log.d("ocrResult", similarityObj.similarity(word, keyword).toString())
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

    fun setBitmapByGallery(bitmap: Bitmap?) {
        if (bitmap == null) return
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        _bitmap.value = mutableBitmap
        drawBoxOnBitmap = drawBoxOnBitmap(mutableBitmap, 50)
        croppedBitmap = cropBitmap(mutableBitmap,50)

    }
}