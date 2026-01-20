package com.hapataka.questwalk.ui.main

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetLoginUserIdUseCase
import com.hapataka.questwalk.core.domain.usecase.PostHistoryUseCase
import com.hapataka.questwalk.core.domain.usecase.UpdateUserInfoUseCase
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.model.Location
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.LocationEntity
import com.hapataka.questwalk.domain.entity.UserEntity
import com.hapataka.questwalk.domain.repository.ImageRepository
import com.hapataka.questwalk.domain.repository.LocationRepository
import com.hapataka.questwalk.domain.repository.OcrRepository
import com.hapataka.questwalk.domain.repository.QuestStackRepository
import com.hapataka.questwalk.domain.repository.UserRepo
import com.hapataka.questwalk.domain.usecase.AchievementListener
import com.hapataka.questwalk.domain.usecase.QuestFilteringUseCase
import com.hapataka.questwalk.util.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import info.debatty.java.stringsimilarity.RatcliffObershelp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

const val QUEST_STOP = 0
const val QUEST_START = 1
const val QUEST_SUCCESS = 2
const val SHOW_LOADING = true
const val HIDE_LOADING = false

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val questRepo: QuestStackRepository,
    private val imageRepo: ImageRepository,
    private val ocrRepo: OcrRepository,
    private val locationRepo: LocationRepository,
    private val imageUtil: ImageUtil,
    private val getLoginUserIdUseCase: GetLoginUserIdUseCase,
    private val postHistoryUseCase: PostHistoryUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase,
) : ViewModel() {
    private var _currentKeyword = MutableLiveData<String>()
    val currentKeyword: LiveData<String> get() = _currentKeyword

    private val _keywordLevel = MutableStateFlow(0)
    val keywordLevel = _keywordLevel.asStateFlow()

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
    private var locationHistory = mutableListOf<Location>()
    private var questLocation: Location? = null
    private var currentTime: String = ""

    private var isPreProcess = false

    fun setCaptureImage(
        image: ImageProxy,
        croppedImage: Bitmap?,
        navigateCallback: () -> Unit,
        visibleImageCallback: (Bitmap) -> Unit,
        invisibleImageCallback: () -> Unit,
    ) {
        if (croppedImage == null) return
        visibleLoading(SHOW_LOADING)

        val bitmapImage = imageUtil.setCaptureImage(image)
        visibleImageCallback(bitmapImage)
        viewModelScope.launch {
            if (!getTextFromOCR(croppedImage, navigateCallback, invisibleImageCallback)) {
                val preProcessImage = imageUtil.preProcessBitmap(croppedImage)
                isPreProcess = true
                getTextFromOCR(preProcessImage!!, navigateCallback, invisibleImageCallback)
            }
        }
    }

    private suspend fun getTextFromOCR(
        image: Bitmap,
        visibleImageCallback: () -> Unit,
        invisibleImageCallback: () -> Unit,
    ): Boolean {
        val element = ocrRepo.getWordFromImage(image)
        val keyword = currentKeyword.value ?: ""
        val checkFail = validationResponseByMLKit(keyword, element)

        delay(1500L)

        if (checkFail) {
            val loc = locationRepo.getCurrent().location
            questLocation = Location(latitude = loc.first, longitude = loc.second)
            _playState.value = QUEST_SUCCESS
            visibleLoading(HIDE_LOADING)
            visibleImageCallback()
        }
        if (isPreProcess) {
            _snackBarMsg.value = "키워드가 보이게 사진을 다시 찍어주세요"
            isPreProcess = false
            visibleLoading(HIDE_LOADING)
            invisibleImageCallback()
        }
        return checkFail
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

    suspend fun stopPlay(callback: (String) -> Unit) {
        val distance = totalDistance.value ?: 0f
        val playState = playState.value ?: 0

        if (distance > -1f) {
            setCurrentLocationInfo(locationRepo.getCurrent())

            _isStop.value = false
            _playState.value = QUEST_STOP
            visibleLoading(SHOW_LOADING)
            setResultHistory(callback, playState == QUEST_SUCCESS)
        }
        _isStop.value = false
        _playState.value = QUEST_STOP
        initPlayInfo()
    }

    fun moveToResult(callback: (resultId: String) -> Unit) {
        viewModelScope.launch {
            callback(UserInfo.uid)
        }
    }

    fun visibleLoading(show: Boolean) {
        _isLoading.value = show
    }

    private fun setCurrentLocationInfo(locationInfo: LocationEntity) {
        val loc = locationInfo.location
        locationHistory += Location(latitude = loc.first, longitude = loc.second)
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
                val loc = it.location
                locationHistory += Location(latitude = loc.first, longitude = loc.second)
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
        navigateCallback: (String) -> Unit,
        isSuccess: Boolean,
    ) {
        val result = makeResult(isSuccess)

        if (isSuccess) {
            updateQuestStack(result.imageUrl.toString())
        }

        viewModelScope.launch {
            launch {
                updateUserInfoUseCase(
                    time = durationTime.value ?: 0L,
                    distance = totalDistance.value ?: 0f,
                    step = totalStep.value ?: 0L,
                    keyword = currentKeyword.value ?: "",
                )
            }
            launch {
                postHistoryUseCase(result)
                    .onSuccess { resultId ->
                        moveToResult { navigateCallback(resultId) }
                    }
                    .onFailure { e ->
                        Log.e(this.javaClass.simpleName, e.message.orEmpty())
                    }
            }


        }.join()

        visibleLoading(HIDE_LOADING)
        resetRecord()
        checkAchievement(userRepo.getInfo(getLoginUserIdUseCase().getOrThrow()))
        setRandomKeyword()

    }

    private suspend fun makeResult(isSuccess: Boolean): History.QuestResult {
        val localImage = imageUtil.getImageUri()
        val userId = getLoginUserIdUseCase().getOrThrow()
        val imageUrl = if (isSuccess) {
            imageRepo.setImage(localImage, userId).toString()
        } else {
            null
        }

        return History.QuestResult(
            id = "quest_${userId}_${System.currentTimeMillis()}",
            userId = userId,
            registerAt = LocalDateTime.now(),
            questKeyword = currentKeyword.value ?: "",
            duration = durationTime.value ?: 0L,
            distance = totalDistance.value ?: 0f,
            step = totalStep.value ?: 0L,
            isSuccess = isSuccess,
            route = locationHistory,
            successLocation = questLocation,
            imageUrl = imageUrl,
        )
    }

    private suspend fun checkAchievement(user: UserEntity) {
        val achieveId = AchievementListener(user)
        val userId = getLoginUserIdUseCase().getOrThrow()

        achieveId.forEach { id ->
            val userAchieveResults =
                user.histories.filterIsInstance<HistoryEntity.AchieveResultEntity>()

            if (userAchieveResults.none() { it.achievementId == id }) {
                userRepo.updateHistoryInfo(
                    userId,
                    History.Achievement(
                        id = "achievement_${userId}_${id}_${System.currentTimeMillis()}",
                        userId = userId,
                        registerAt = LocalDateTime.now(),
                        achievementId = id,
                        description = "",
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
            val newQuest = QuestFilteringUseCase().invoke().random()

            _keywordLevel.update { newQuest.level }
            _currentKeyword.value = newQuest.keyWord
        }
    }

    fun setSelectKeyword(keyword: String) {
        viewModelScope.launch {
            val newQuest =
                QuestFilteringUseCase().invoke().find { it.keyWord == keyword } ?: return@launch

            _keywordLevel.update { newQuest.level }
            _currentKeyword.value = newQuest.keyWord
        }
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