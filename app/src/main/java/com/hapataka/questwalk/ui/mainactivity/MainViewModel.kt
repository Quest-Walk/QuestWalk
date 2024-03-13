package com.hapataka.questwalk.ui.mainactivity

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.cloudvision.repository.OcrRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.ImageRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.data.fusedlocation.repository.LocationRepositoryImpl
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.usecase.QuestFilteringUseCase
import com.hapataka.questwalk.ui.record.TAG
import info.debatty.java.stringsimilarity.RatcliffObershelp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

const val QUEST_STOP = 0
const val QUEST_START = 1
const val QUEST_SUCCESS = 2

class MainViewModel(
    private val authRepo: AuthRepositoryImpl,
    private val userRepo: UserRepositoryImpl,
    private val questRepo: QuestStackRepositoryImpl,
    private val achieveRepo: AuthRepositoryImpl,
    private val imageRepo: ImageRepositoryImpl,
    private val ocrRepo: OcrRepositoryImpl,
    private val locationRepo: LocationRepositoryImpl,
    private val imageUtil: ImageUtil
) : ViewModel() {
    private var _currentKeyword = MutableLiveData<String>()
    private var _imageBitmap = MutableLiveData<Bitmap>()
    private var _playState = MutableLiveData(QUEST_STOP)
    private var _durationTime = MutableLiveData<Long>(-1)
    private var _totalDistance = MutableLiveData<Float>(0.0F)
    private var _totalStep = MutableLiveData<Long>()
    private var _snackBarMsg = MutableLiveData<String>()

    val currentKeyword: LiveData<String> get() = _currentKeyword
    val imageBitmap: LiveData<Bitmap> get() = _imageBitmap
    val playState: LiveData<Int> get() = _playState
    val durationTime: LiveData<Long> get() = _durationTime
    val totalDistance: LiveData<Float> get() = _totalDistance
    val totalStep: LiveData<Long> get() = _totalStep
    val snackBarMsg: LiveData<String> get() = _snackBarMsg

    private var timer: Job? = null
    private var locationHistory = mutableListOf<Pair<Float, Float>>()
    private var questLocation: Pair<Float, Float>? = null

    init {
        setRandomKeyword()
    }

    fun moveToResult(callback: (string: String) -> Unit) {
        viewModelScope.launch {
            callback(authRepo.getCurrentUserUid())
        }
    }

    fun setCaptureImage(image: ImageProxy, callback: () -> Unit) {
        val bitmapImage = imageUtil.setCaptureImage(image)

        getTextFromOCR(bitmapImage, callback)
    }

    private fun getTextFromOCR(image: Bitmap, callback: () -> Unit) {
        viewModelScope.launch {
            val element = ocrRepo.getWordFromImage(image)
            val keyword = currentKeyword.value ?: ""
            val checkFail = validationResponseByMLKit(keyword, element)

            if (checkFail) {
                _playState.value = QUEST_SUCCESS
                callback()
            } else {
                // TODO: 실패상황처리
                Log.e(TAG, "실패함")
            }
        }
    }

    fun togglePlay(callback: () -> Unit) {
        val playState = playState.value ?: 0

        if (playState == QUEST_STOP) {
            _playState.value = QUEST_START
        } else {
            setResultHistory(callback)
            _playState.value = QUEST_STOP
        }
        initPlayInfo()
    }

    private fun initPlayInfo() {
        setTimer()
        setLocationClient()
    }

    private fun setTimer() {
        if (playState.value != QUEST_STOP) {
            timer = viewModelScope.launch {
                while (true) {
                    var currentTime = durationTime.value!!

                    _durationTime.value = currentTime + 1
                    delay(1000L)
                }
            }
        } else {
            timer?.cancel()
        }
    }

    private fun setLocationClient() {
        if (playState.value != QUEST_STOP) {
            locationRepo.startRequest {
                setDistance(it.distance)
                locationHistory += it.location
            }
        } else {
            locationRepo.finishRequest()
        }
    }

    private fun setResultHistory(callback: () -> Unit) {
        viewModelScope.launch {
            val uid = authRepo.getCurrentUserUid()
            val isFail = playState.value != QUEST_SUCCESS
            val currentTime = LocalDateTime.now().toString()
            val localImage = imageUtil.getImageUri()
            val imageUri = if (playState.value == QUEST_SUCCESS) {
                imageRepo.setImage(localImage, uid).toString()
            } else {
                null
            }
            val result = HistoryEntity.ResultEntity(
                currentTime,
                currentKeyword.value ?: "",
                durationTime.value ?: 0L,
                totalDistance.value ?: 0f,
                totalStep.value ?: 0L,
                isFail,
                locationHistory,
                questLocation,
                imageUri,
            )

            if (isFail.not()) {
                val keyword = currentKeyword.value ?: ""

                questRepo.updateQuest(keyword, uid, imageUri!!, currentTime)
            }
            userRepo.updateUserInfo(uid, result)
            _totalDistance.value = 0f
            _durationTime.value = -1
            _totalStep.value = 1
            setRandomKeyword()
            callback()
        }
    }

    private fun setDistance(distance: Float) {
        val currentDistance = totalDistance.value ?: 0f

        _totalDistance.value = currentDistance + distance
    }

    private fun setRandomKeyword() {
        viewModelScope.launch {
            val remainingKeyword = QuestFilteringUseCase().invoke().map { it.keyWord }

            _currentKeyword.value = remainingKeyword.random()
        }
    }

    fun setSelectKeyword(keyword: String) {
        _currentKeyword.value = keyword
    }

    private fun validationResponseByMLKit(keyword: String, elements: List<String>): Boolean {
        var isValidated = false
        val similarityObj = RatcliffObershelp()

        elements.forEach { element ->
            if (element.contains(keyword)) {
                isValidated = true
                return@forEach
            } else if (similarityObj.similarity(element, keyword) >= 0.6) {
                isValidated = true
                return@forEach
            }
        }
        return isValidated
    }

    fun setSnackBarMsg(msg: String) {
        _snackBarMsg.value = msg
    }

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