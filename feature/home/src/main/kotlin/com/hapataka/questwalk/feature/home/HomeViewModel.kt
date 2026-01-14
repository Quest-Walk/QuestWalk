package com.hapataka.questwalk.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _timeState = MutableStateFlow(LocalTime.now().hour)
    val timeState = _timeState.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                _timeState.update { LocalTime.now().hour }
                delay(60000L) // Update every minute
            }
        }
    }
}
