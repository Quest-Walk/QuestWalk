package com.hapataka.questwalk.ui.home

import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationResult
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.ImageRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.usecase.QuestFilteringUseCase
import com.hapataka.questwalk.ui.record.TAG
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime

class HomeViewModel(
    private val authRepo: AuthRepositoryImpl,
    private val userRepo: UserRepositoryImpl,
    private val imageRepo: ImageRepositoryImpl,
    private val questRepo: QuestStackRepositoryImpl
) : ViewModel() {
    private var _currentKeyword = MutableLiveData<String>()
    private var _isPlay = MutableLiveData(false)
    private var _durationTime = MutableLiveData<Long>(0)
    private var _isNight = MutableLiveData(false)
    private var _totalStep = MutableLiveData<Int>()
    private var _totalDistance = MutableLiveData<Float>(0.0F)
    private var _isLoading = MutableLiveData<Boolean>(false)
    private var _isEnabledButton = MutableLiveData<Boolean>(true)
    private var _charNum = MutableLiveData<Int>()

    val currentKeyword: LiveData<String> get() = _currentKeyword
    val isPlay: LiveData<Boolean> get() = _isPlay
    val durationTime: LiveData<Long> get() = _durationTime
    val isNight: LiveData<Boolean> get() = _isNight
    val totalStep: LiveData<Int> get() = _totalStep
    val totalDistance: LiveData<Float> get() = _totalDistance
    val isLoading: LiveData<Boolean> get() = _isLoading
    val isEnabledButton: LiveData<Boolean> get() = _isEnabledButton

    val charNum : LiveData<Int> get() = _charNum

    private var prevLocation: Location? = null
    private val filteringUseCase = QuestFilteringUseCase()
    private var timer: Job? = null

    private var locationHistory = mutableListOf<Pair<Float, Float>>()
    private var questLocation: Pair<Float, Float>? = null

    var time = LocalTime.now().hour

    init {
        getRandomKeyword()
    }

    fun checkCurrentTime() {
        when (time) {
            in 7..18 -> _isNight.value = false
            else -> _isNight.value = true
        }
    }

    private fun getRandomKeyword() {
        Log.d("getRandomKeyword","getRandomKeyword: Run")
//        if (currentKeyword.value.isNullOrEmpty()) {
            viewModelScope.launch {
                val remainingKeyword = filteringUseCase().map { it.keyWord }

                _currentKeyword.value = remainingKeyword.random()
//            }
        }
    }

    fun setKeyword(keyword: String) {
        _currentKeyword.value = keyword
    }

    fun toggleIsPlay() {
        _isPlay.value = isPlay.value?.not()
        toggleTimer()
        _totalDistance.value = 0f
        _totalStep.value = 0
        locationHistory.clear()
        prevLocation = null
    }

    fun toggleIsPlay(callBack: (String, String?, String) -> Unit) {
        _isPlay.value = isPlay.value?.not()
        toggleTimer()

        if (!isPlay.value!!) {
            viewModelScope.launch {
                val uid = authRepo.getCurrentUserUid()
                val registerAt = LocalTime.now().toString()
                if (imageUri != null) {
                    _isLoading.value = true
                    val remoteUri = imageRepo.setImage(imageUri!!, uid)
                    Log.i(TAG, "quest: ${questLocation}")
                    val result = HistoryEntity.ResultEntity(
                        registerAt,
                        currentKeyword.value ?: "",
                        durationTime.value ?: 0,
                        totalDistance.value ?: 0f,
                        totalStep.value ?: 0,
                        false,
                        locationHistory,
                        questLocation,
                        remoteUri.toString()
                    )

                    userRepo.updateUserInfo(uid, result)
                    questRepo.updateQuest(currentKeyword.value!!, uid, remoteUri.toString(), registerAt)
                    getRandomKeyword()
                } else {
                    val result = HistoryEntity.ResultEntity(
                        registerAt,
                        currentKeyword.value ?: "",
                        durationTime.value ?: 0,
                        totalDistance.value ?: 0f,
                        totalStep.value ?: 0,
                        true,
                        locationHistory
                    )

                    userRepo.updateUserInfo(uid, result)
                }
                _totalDistance.value = 0f
                _totalStep.value = 0
                locationHistory.clear()
                prevLocation = null
                imageUri = null
                questLocation = null
                _isLoading.value = false
                callBack(uid, currentKeyword.value ?: "", registerAt)
            }
        }
    }

    private fun toggleTimer() {
        if (isPlay.value!!) {
            timer = viewModelScope.launch {
                _durationTime.value = 0L

                while (true) {
                    var currentTime = durationTime.value!!
                    _isEnabledButton.value = currentTime !in 0L..20L

                    delay(1000L)
                    _durationTime.value = currentTime + 1
                }
            }
        } else {
            timer?.cancel()
        }
    }

    fun updateStep() {
        Log.i(TAG, "update step")
        if (isPlay.value!!) {
            val currentStep = totalStep.value ?: 0

            _totalStep.value = currentStep + 1
            Log.i(TAG, "step: ${totalStep.value}")
        } else {
            _totalStep.value = 0
        }
    }

    private var imageUri: Uri? = null
    fun setImageBitmap(image: String) {
        Log.d(TAG, "bitmap: ${image}")
        imageUri = Uri.parse("file://$image")
    }

    fun updateLocation(locationResult: LocationResult) {
        val currentLocation = locationResult.locations.last()
        val currentDistance = totalDistance.value ?: 0f
        val moveDistance = currentLocation.distanceTo(
            if (prevLocation != null) prevLocation!! else currentLocation
        )

        if (prevLocation == null) {
            prevLocation=currentLocation
            locationHistory += Pair(currentLocation.latitude.toFloat(), currentLocation.longitude.toFloat())
        }

        prevLocation = currentLocation

        if (currentLocation.hasAccuracy().not()) {
            return
        }

        if (currentLocation.accuracy > 30) {
            return
        }

        if(currentLocation.accuracy*1.5 >= moveDistance){
            return
        }
        _totalDistance.value = currentDistance + if (moveDistance < 1f) 0f else moveDistance
        locationHistory += Pair(currentLocation.latitude.toFloat(), currentLocation.longitude.toFloat())
    }

    fun setQuestSuccessLocation() {
        questLocation = locationHistory.last()
    }

    private fun getUserCharNum() {
        viewModelScope.launch {
            val userId = authRepo.getCurrentUserUid()

            val userInfo = userRepo.getInfo(userId)
            _charNum.value = userInfo.characterId
        }

    }

}