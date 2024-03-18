package com.hapataka.questwalk.ui.mainactivity

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.ResultEntity
import com.hapataka.questwalk.domain.repository.AchieveStackRepository
import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.domain.repository.ImageRepository
import com.hapataka.questwalk.domain.repository.LocationRepository
import com.hapataka.questwalk.domain.repository.OcrRepository
import com.hapataka.questwalk.domain.repository.QuestStackRepository
import com.hapataka.questwalk.domain.repository.UserRepository
import com.hapataka.questwalk.domain.usecase.AchievementListener
import com.hapataka.questwalk.domain.usecase.QuestFilteringUseCase
import com.hapataka.questwalk.ui.record.TAG
import com.hapataka.questwalk.util.UserInfo
import info.debatty.java.stringsimilarity.RatcliffObershelp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

const val QUEST_STOP = 0
const val QUEST_START = 1
const val QUEST_SUCCESS = 2

class MainViewModel(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val questRepo: QuestStackRepository,
    private val achieveRepo: AchieveStackRepository,
    private val imageRepo: ImageRepository,
    private val ocrRepo: OcrRepository,
    private val locationRepo: LocationRepository,
    private val imageUtil: ImageUtil
) : ViewModel() {
    private var _currentKeyword = MutableLiveData<String>()
    val currentKeyword: LiveData<String> get() = _currentKeyword

    private var _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap> get() = _imageBitmap

    private var _playState = MutableLiveData(QUEST_STOP)
    val playState: LiveData<Int> get() = _playState

    private var _durationTime = MutableLiveData<Long>(-1)
    val durationTime: LiveData<Long> get() = _durationTime


    private var _totalDistance = MutableLiveData<Float>(0.0F)
    val totalDistance: LiveData<Float> get() = _totalDistance

    private var _totalStep = MutableLiveData<Long>()
    val totalStep: LiveData<Long> get() = _totalStep

    private var _snackBarMsg = MutableLiveData<String>()
    val snackBarMsg: LiveData<String> get() = _snackBarMsg

    private var _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var timer: Job? = null
    private var locationHistory = mutableListOf<Pair<Float, Float>>()
    private var questLocation: Pair<Float, Float>? = null
    private var currentTime: String = ""

    fun moveToResult(callback: (uid: String, registerAt: String) -> Unit) {
        viewModelScope.launch {
            callback(UserInfo.uid, currentTime)
        }
    }

    fun setCaptureImage(
        image: ImageProxy,
        navigateCallback: () -> Unit,
        imageCallback: (Bitmap) -> Unit
    ) {
        val bitmapImage = imageUtil.setCaptureImage(image)

        imageCallback(bitmapImage)
        getTextFromOCR(bitmapImage, navigateCallback)
    }

    private fun getTextFromOCR(image: Bitmap, callback: () -> Unit) {
        viewModelScope.launch {
            val element = ocrRepo.getWordFromImage(image)
            val keyword = currentKeyword.value ?: ""
            val checkFail = validationResponseByMLKit(keyword, element)

            if (checkFail) {
                questLocation = locationRepo.getCurrent().location
                _playState.value = QUEST_SUCCESS
                callback()
            } else {
                // TODO: 실패상황처리
                Log.e(TAG, "실패함")
            }
        }
    }

    fun togglePlay(callback: (String, String) -> Unit) {
        val playState = playState.value ?: 0

        viewModelScope.launch {
            val locationInfo = locationRepo.getCurrent()

            locationHistory += locationInfo.location

            if (playState == QUEST_STOP) {
                _playState.value = QUEST_START
            } else {
                setResultHistory(callback)
                _playState.value = QUEST_STOP
                _totalDistance.value = locationInfo.distance
            }

            if (playState == QUEST_SUCCESS) {
                _isLoading.value = true
            }
            initPlayInfo()
        }
    }

    private fun initPlayInfo() {
        setTimer()
        setLocationClient()
        setStepCounter()
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

    private fun setStepCounter() {
        if (playState.value == QUEST_STOP) {
            _totalStep.value = 0
        }
    }

    fun countUpStep() {
        val currentStep = totalStep.value ?: 0

        _totalStep.value = currentStep + 1
        return
    }

    private fun setResultHistory(navigateResult: (String, String) -> Unit) {
        viewModelScope.launch {
            val result = makeResult()

            if (playState.value == QUEST_SUCCESS) {
                updateQuestStack(result.questImg.toString())
            }
            userRepo.updateUserInfo(UserInfo.uid, result)
            moveToResult { uid, registerAt ->
                navigateResult(uid, registerAt)
            }
            resetRecord()

            val userInfo = userRepo.getInfo(UserInfo.uid)
            val achieveId = AchievementListener(userInfo)

            achieveId.forEach { id ->
                val userAchieveResults =
                    userInfo.histories.filterIsInstance<HistoryEntity.AchieveResultEntity>()

                if (userAchieveResults.none() { it.achievementId == id }) {
                    userRepo.updateUserInfo(
                        UserInfo.uid,
                        HistoryEntity.AchieveResultEntity(
                            currentTime,
                            id
                        )
                    )
                    _snackBarMsg.value = "업적달성"
                }
            }
        }
    }

    private suspend fun makeResult(): ResultEntity {
        currentTime = LocalDateTime.now().toString()
        val isSuccess = playState.value == QUEST_SUCCESS
        val localImage = imageUtil.getImageUri()
        val imageUri = if (isSuccess) {
            imageRepo.setImage(localImage, UserInfo.uid).toString()
        } else {
            null
        }

        return ResultEntity(
            currentTime,
            currentKeyword.value ?: "",
            durationTime.value ?: 0L,
            totalDistance.value ?: 0f,
            totalStep.value ?: 0L,
            isSuccess,
            locationHistory,
            questLocation,
            imageUri,
        )
    }

    private suspend fun updateQuestStack(uri: String) {
        val keyword = currentKeyword.value ?: ""

        questRepo.updateQuest(
            keyword,
            UserInfo.uid,
            uri,
            currentTime
        )
    }

    private fun resetRecord() {
        _totalDistance.value = 0f
        _durationTime.value = -1
        _totalStep.value = 1
        _isLoading.value = false
        setRandomKeyword()
    }

    private fun setDistance(distance: Float) {
        val currentDistance = totalDistance.value ?: 0f
        val result = currentDistance + distance

        _totalDistance.value = result
    }

    fun setRandomKeyword() {
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