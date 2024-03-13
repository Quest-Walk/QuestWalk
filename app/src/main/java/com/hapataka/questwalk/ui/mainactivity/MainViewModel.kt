package com.hapataka.questwalk.ui.mainactivity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.data.fusedlocation.repository.LocationRepositoryImpl
import com.hapataka.questwalk.ui.record.TAG
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepo: AuthRepositoryImpl,
    private val userRepo: UserRepositoryImpl,
    private val questRepo: QuestStackRepositoryImpl,
    private val achieveRepo: AuthRepositoryImpl,
    private val locationRepo: LocationRepositoryImpl
) : ViewModel() {
    private var _currentUserId = MutableLiveData<String>()
    private var _imageBitmap = MutableLiveData<Bitmap>()
    private var _isPlay = MutableLiveData(false)
    val isPlay: LiveData<Boolean> get() = _isPlay
    val currentUserId: LiveData<String> get() = _currentUserId
    val imageBitmap: LiveData<Bitmap> get() = _imageBitmap

    private var resultElement: List<String> = listOf()


    fun moveToResult(callback: (string: String) -> Unit) {
        viewModelScope.launch {
            callback(authRepo.getCurrentUserUid())
        }
    }

    fun setCaptureImage(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())

        buffer.get(bytes)

        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val matrix = Matrix()

        matrix.postRotate(image.imageInfo.rotationDegrees.toFloat())
        val postBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        _imageBitmap.value = postBitmap
        getTextFromOCR()
    }

    fun getTextFromOCR() {
        // OCR빌더
    }

    fun location() {
        locationRepo.startRequest {
            Log.i(TAG, "location: $it")
        }
    }

//    private fun validationResponseByMLKit(keyword: String): Boolean {
//        var isValidated = false
//        val similarityObj = RatcliffObershelp()
//
//        resultElement.forEach { element: Text.Element ->
//            val word = element.text
//            Log.d("ocrResult", word)
//            if (word.contains(keyword)) {
//                isValidated = true
//                return@forEach
//            } else if (similarityObj.similarity(word, keyword) >= 0.6) {
//                isValidated = true
//                return@forEach
//            }
//            Log.d("ocrResult", similarityObj.similarity(word, keyword).toString())
//        }
//        if (isValidated) {
//            file = repository.saveBitmap(bitmap.value!!, "resultImage.png")
//        } else {
//            file = null
//            repository.deleteBitmap()
//        }
//        return isValidated
//    }

//    fun failedImageDrawWithCanvasByMLKit(keyword: String) {
//        val tempBitmap = _bitmap.value ?: return
//        val canvas = Canvas(tempBitmap)
//        val paint = Paint().apply {
//            color = Color.RED
//            style = Paint.Style.STROKE
//            strokeWidth = 4f
//        }
//        val similarityObj = RatcliffObershelp()
//        val keywordPaint = Paint().apply {
//            color = Color.BLUE
//            style = Paint.Style.STROKE
//            strokeWidth = 4f
//        }
//        resultListByMLKit.forEach { element ->
//            val word = element.text
//            if (similarityObj.similarity(word, keyword) >= 0.2)
//                canvas.drawRect(element.boundingBox!!, keywordPaint)
//            else
//                canvas.drawRect(element.boundingBox!!, paint)
//        }
//
//        _bitmap.value = tempBitmap
//        initIsSucceed()
//    }


}