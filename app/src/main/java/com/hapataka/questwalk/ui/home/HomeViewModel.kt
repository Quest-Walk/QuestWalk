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

class HomeViewModel : ViewModel() {
    private var _currentKeyword = MutableLiveData<String>()
    private var _isPlay = MutableLiveData<Boolean>(false)
    private var _durationTime = MutableLiveData<Long>(0)

    val currentKeyword: LiveData<String> get() = _currentKeyword
    val isPlay: LiveData<Boolean> get() = _isPlay
    val durationTime: LiveData<Long> get() = _durationTime


    private val filteringUseCase = QuestFilteringUseCase()
    private var timer: Job? = null

    private var imgPath: Uri? = null
    var isQuestSuccess: Boolean = false

    fun getRandomKeyword() {
        if (currentKeyword.value.isNullOrEmpty()) {
            viewModelScope.launch {
                val remainingKeyword = filteringUseCase().map { it.keyWord }

                _currentKeyword.value = remainingKeyword.random()
            }
        }
    }

    fun toggleIsPlay() {
        _isPlay.value = isPlay.value?.not()
        toggleTimer()
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