package com.hapataka.questwalk.ui.home

import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.ImageRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.usecase.QuestFilteringUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepo: AuthRepositoryImpl,
    private val userRepo: UserRepositoryImpl,
    private val imageRepo: ImageRepositoryImpl
) : ViewModel() {
    private var _currentKeyword = MutableLiveData<String>()
    private var _isPlay = MutableLiveData<Boolean>(false)
    private var _durationTime = MutableLiveData<Long>(0)
    private var _totalStep = MutableLiveData<Int>()
    private var _totalDistance = MutableLiveData<Float>(0.0F)
    private var _imagePath = MutableLiveData<Uri>()

    val currentKeyword: LiveData<String> get() = _currentKeyword
    val isPlay: LiveData<Boolean> get() = _isPlay
    val durationTime: LiveData<Long> get() = _durationTime
    val totalStep: LiveData<Int> get() = _totalStep
    val totalDistance: LiveData<Float> get() = _totalDistance
    val imagePath: LiveData<Uri> get() = _imagePath

    private val filteringUseCase = QuestFilteringUseCase()
    private var timer: Job? = null

    private lateinit var imgDownloadUrl: Uri
    private var locationHistory = ArrayList<Location>()
    private var preLocation: Location? = null
    private var questLocation: Location? = null

    var isQuestSuccess: Boolean = false

    fun getRandomKeyword() {
        if (currentKeyword.value.isNullOrEmpty()) {
            viewModelScope.launch {
                val remainingKeyword = filteringUseCase().map { it.keyWord }

                _currentKeyword.value = remainingKeyword.random()
            }
        }
    }

    fun setKeyword(keyword: String) {
        _currentKeyword.value = keyword
    }

    fun toggleIsPlay(callBack: () -> Unit) {
        _isPlay.value = isPlay.value?.not()
        toggleTimer()

        if (!isPlay.value!!) {
            // 포기하기 or 완료하기
            callBack()
            if(imagePath.value != null) {
                Log.d("HomeViewModel:","HomeViewModel:$imagePath 진입")
                setImage()
            }
//            updateUserInfo(requestUserInfo())
        }
    }

    private fun toggleTimer() {
        if (isPlay.value!!) {
            timer = viewModelScope.launch {
                _durationTime.value = 0L

                while (true) {
                    var currentTime = durationTime.value!!

                    delay(1000L)
                    _durationTime.value = currentTime + 1
                }
            }
        } else {
            timer?.cancel()
        }
    }

    fun updateStep() {
        if (isPlay.value!!) {
            _totalStep.value?.plus(1)
        } else {
            _totalStep.value = 0
        }
    }

    fun setImagePath(uri: String) {
        _imagePath.value = uri.toUri()
    }

    fun updateLocation(locationResult: LocationResult) {
        locationResult.let {
            for (location in it.locations) {
//                Log.d("HomeViewModel:", "현재위치 ${location.latitude}, ${location.longitude}")
                if (location.hasAccuracy() && (location.accuracy <= 30) && (preLocation != null)) {
                    if (location.accuracy * 1.5 < location.distanceTo(preLocation!!)) {
                        _totalDistance.value = location.distanceTo(preLocation!!)
                        locationHistory.add(location)
                        preLocation = location

                    }
                } else {
                    locationHistory.add(location)
                    preLocation = location
                }
            }
        }
    }

    fun checkQuestLocation() {
        questLocation = locationHistory.lastOrNull()
    }

    private fun Long.convertTime(): String {
        val second = this % 60
        val minute = this / 60
        val displaySecond = if (second < 10) "0$second" else second.toString()
        val displayMinute = when (minute) {
            0L -> "00"
            in 1..9 -> "0$minute"
            else -> minute.toString()
        }

        return "$displayMinute:$displaySecond"
    }

    private fun setImage() {
        viewModelScope.launch {
            val userId = authRepo.getCurrentUserUid()
            imgDownloadUrl = imageRepo.setImage(_imagePath.value!!, userId)
        }
    }

    private fun updateUserInfo(history: HistoryEntity.ResultEntity) {
        viewModelScope.launch {
            val userId = authRepo.getCurrentUserUid()
            userRepo.updateUserInfo(userId, history)
        }
    }

    private fun requestUserInfo(): HistoryEntity.ResultEntity {
        return HistoryEntity.ResultEntity(
            quest = _currentKeyword.value ?: "",
            time = _durationTime.value?.convertTime() ?: "",
            distance = _totalDistance.value ?: 0F,
            step = _totalStep.value ?: 0,
            latitueds = locationHistory.map { it.latitude.toFloat() },
            longitudes = locationHistory.map { it.longitude.toFloat() },
            questLatitued = questLocation?.latitude?.toFloat() ?: 0F,
            questLongitude = questLocation?.longitude?.toFloat() ?: 0F,
            registerAt = "20240307",
            isFailed =  imagePath.value == null,
            questImg = "$imgDownloadUrl"
        )
    }

}