package com.hapataka.questwalk.ui.mainactivity

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.ResultEntity
import com.hapataka.questwalk.domain.entity.LocationEntity
import com.hapataka.questwalk.domain.entity.UserEntity
import com.hapataka.questwalk.domain.repository.ImageRepository
import com.hapataka.questwalk.domain.repository.LocationRepository
import com.hapataka.questwalk.domain.repository.OcrRepository
import com.hapataka.questwalk.domain.repository.QuestStackRepository
import com.hapataka.questwalk.domain.repository.UserRepository
import com.hapataka.questwalk.domain.usecase.AchievementListener
import com.hapataka.questwalk.domain.usecase.QuestFilteringUseCase
import com.hapataka.questwalk.util.UserInfo
import info.debatty.java.stringsimilarity.RatcliffObershelp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

const val QUEST_STOP = 0
const val QUEST_START = 1
const val QUEST_SUCCESS = 2
const val SHOW_LOADING = true
const val HIDE_LOADING = false

class MainViewModel(
    private val userRepo: UserRepository,
    private val questRepo: QuestStackRepository,
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

    private var _isStop = MutableLiveData<Boolean>()
    val isStop: LiveData<Boolean> get() = _isStop

    private var timer: Job? = null
    private var locationHistory = mutableListOf<Pair<Float, Float>>()
    private var questLocation: Pair<Float, Float>? = null
    private var currentTime: String = ""

    fun setCaptureImage(
        image: ImageProxy,
        navigateCallback: () -> Unit,
        visibleImageCallback: (Bitmap) -> Unit,
        invisibleImageCallback: () -> Unit,
    ) {
        visibleLoading(SHOW_LOADING)

        val bitmapImage = imageUtil.setCaptureImage(image)

        visibleImageCallback(bitmapImage)
        getTextFromOCR(bitmapImage, navigateCallback, invisibleImageCallback)
    }

    private fun getTextFromOCR(
        image: Bitmap,
        visibleImageCallback: () -> Unit,
        invisibleImageCallback: () -> Unit
    ) {
        viewModelScope.launch {
            val element = ocrRepo.getWordFromImage(image)
            val keyword = currentKeyword.value ?: ""
            val checkFail = validationResponseByMLKit(keyword, element)

            delay(1500L)
            _isLoading.value = false

            if (checkFail) {
                questLocation = locationRepo.getCurrent().location
                _playState.value = QUEST_SUCCESS
                visibleImageCallback()
            } else {
                _snackBarMsg.value = "키워드가 보이게 사진을 다시 찍어주세요"
                invisibleImageCallback()
            }
        }
    }

    fun togglePlay() {
        viewModelScope.launch {
            val playState = playState.value ?: 0

            setCurrentLocationInfo(locationRepo.getCurrent())

            if (playState == QUEST_STOP) {
                _playState.value = QUEST_START
                initPlayInfo()
                return@launch
            }
            _isStop.value = true
        }
    }

    suspend fun stopPlay(callback: (String, String) -> Unit) {
        val distance = totalDistance.value ?: 0f
        val playState = playState.value ?: 0

        if (distance > 10f) {
            setCurrentLocationInfo(locationRepo.getCurrent())

            _isStop.value = false
            _playState.value = QUEST_STOP
            visibleLoading(SHOW_LOADING)
            setResultHistory(callback, playState == QUEST_SUCCESS)
        }
        initPlayInfo()
    }

    fun moveToResult(callback: (uid: String, registerAt: String) -> Unit) {
        viewModelScope.launch {
            callback(UserInfo.uid, currentTime)
        }
    }

    fun visibleLoading(show: Boolean) {
        _isLoading.value = show
    }

    private fun setCurrentLocationInfo(locationInfo: LocationEntity) {
        locationHistory += locationInfo.location
        _totalDistance.value = _totalDistance.value?.plus(locationInfo.distance)
    }

    fun resumePlay() {
        _isStop.value = false
    }

    private fun initPlayInfo() {
        setTimer()
        setLocationClient()
        setStepCounter()
    }

    private fun setTimer() {
        if (playState.value != QUEST_STOP) {
            timer = viewModelScope.launch {
                _durationTime.value = 0

                while (true) {
                    val currentTime = durationTime.value!!

                    delay(1000L)
                    _durationTime.value = currentTime + 1
                }
            }
        } else {
            timer?.cancel()
        }
    }

    private fun setLocationClient() {
        if (playState.value != QUEST_STOP) {
            locationHistory.clear()
            questLocation = null
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

    private suspend fun setResultHistory(
        navigateCallback: (String, String) -> Unit,
        isSuccess: Boolean
    ) {
        val result = makeResult(isSuccess)

        if (isSuccess) {
            updateQuestStack(result.questImg.toString())
        }
        userRepo.updateHistoryInfo(UserInfo.uid, result)
        visibleLoading(HIDE_LOADING)
        resetRecord()
        checkAchievement(userRepo.getInfo(UserInfo.uid))
        setRandomKeyword()
        moveToResult { uid, registerAt ->
            navigateCallback(uid, registerAt)
        }
    }

    private suspend fun makeResult(isSuccess: Boolean): ResultEntity {
        val localImage = imageUtil.getImageUri()
        val imageUri = if (isSuccess) {
            imageRepo.setImage(localImage, UserInfo.uid).toString()
        } else {
            null
        }

        currentTime = LocalDateTime.now().toString()
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

    private suspend fun checkAchievement(user: UserEntity) {
        val achieveId = AchievementListener(user)

        achieveId.forEach { id ->
            val userAchieveResults =
                user.histories.filterIsInstance<HistoryEntity.AchieveResultEntity>()

            if (userAchieveResults.none() { it.achievementId == id }) {
                userRepo.updateHistoryInfo(
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
        _totalStep.value = 0
        _isStop.value = false
    }

    private fun setDistance(distance: Float) {
        if (distance > 30f) return

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
}