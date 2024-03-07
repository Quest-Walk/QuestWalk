package com.hapataka.questwalk.ui.home

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.usecase.QuestFilteringUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime

class HomeViewModel : ViewModel() {
    private var _currentKeyword = MutableLiveData<String>()
    private var _isPlay = MutableLiveData(false)
    private var _durationTime = MutableLiveData<Long>(0)
    private var _isNight = MutableLiveData(false)

    val currentKeyword: LiveData<String> get() = _currentKeyword
    val isPlay: LiveData<Boolean> get() = _isPlay
    val durationTime: LiveData<Long> get() = _durationTime
    val isNight: LiveData<Boolean> get() = _isNight


    private val filteringUseCase = QuestFilteringUseCase()
    private var timer: Job? = null

    private var imgPath: Uri? = null
    var isQuestSuccess: Boolean = false


    fun checkCurrentTime() {
        when (LocalTime.now().hour) {
            in 7 .. 18 -> _isNight.value = false
            else -> _isNight.value = true
        }
    }
    fun getRandomKeyword() {
        if (currentKeyword.value.isNullOrEmpty()) {
            viewModelScope.launch {
                val remainingKeyword = filteringUseCase().map { it.keyWord }

                _currentKeyword.value = remainingKeyword.random()
            }
        }
    }

    fun toggleIsPlay(callBack: () -> Unit) {
        _isPlay.value = isPlay.value?.not()
        toggleTimer()
        // TODO: 모험하기 눌렸을때 실행되는 코드들

        if (!isPlay.value!!){
            callBack()
        }
    }

    fun setKeyword(keyword: String) {
        _currentKeyword.value = keyword
    }

    fun setImagePath(uri: String) {
        imgPath = uri.toUri()
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
}