package com.hapataka.questwalk.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.service.PlaySessionService
import com.hapataka.questwalk.core.domain.usecase.GetCurrentQuestUseCase
import com.hapataka.questwalk.core.domain.usecase.GetPlaySessionUseCase
import com.hapataka.questwalk.core.domain.usecase.IncrementStepUseCase
import com.hapataka.questwalk.core.domain.usecase.ResetPlaySessionUseCase
import com.hapataka.questwalk.core.domain.usecase.SelectQuestUseCase
import com.hapataka.questwalk.core.domain.usecase.SelectRandomQuestUseCase
import com.hapataka.questwalk.core.domain.usecase.StartPlaySessionUseCase
import com.hapataka.questwalk.core.domain.usecase.StopPlaySessionUseCase
import com.hapataka.questwalk.core.model.PlayState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentQuestUseCase: GetCurrentQuestUseCase,
    private val selectRandomQuestUseCase: SelectRandomQuestUseCase,
    private val selectQuestUseCase: SelectQuestUseCase,
    private val getPlaySessionUseCase: GetPlaySessionUseCase,
    private val startPlaySessionUseCase: StartPlaySessionUseCase,
    private val stopPlaySessionUseCase: StopPlaySessionUseCase,
    private val incrementStepUseCase: IncrementStepUseCase,
    private val resetPlaySessionUseCase: ResetPlaySessionUseCase,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        initializeQuestIfNeeded()
        observeState()
        updateCurrentTime()
    }

    private fun initializeQuestIfNeeded() {
        viewModelScope.launch {
            if (getCurrentQuestUseCase().first() == null) {
                selectRandomQuestUseCase()
            }
        }
    }

    private fun observeState() {
        viewModelScope.launch {
            combine(
                getCurrentQuestUseCase(),
                getPlaySessionUseCase(),
            ) { quest, session ->
                _uiState.value.copy(
                    currentKeyword = quest?.keyword ?: "",
                    keywordLevel = quest?.level ?: 0,
                    playState = session.playState.toIntState(),
                    duration = formatDuration(session.duration),
                    step = session.steps.toString(),
                    distance = formatDistance(session.distance),
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun updateCurrentTime() {
        viewModelScope.launch {
            while (true) {
                _uiState.update { it.copy(currentTime = LocalTime.now().hour) }
                delay(60000L)
            }
        }
    }

    fun startSession() {
        runCatching { startPlaySessionUseCase() }
        PlaySessionService.start(context)
    }

    fun stopSession() {
        stopPlaySessionUseCase()
        PlaySessionService.stop(context)
    }

    fun resetSession() {
        viewModelScope.launch {
            resetPlaySessionUseCase()
            selectRandomQuestUseCase()
        }
        PlaySessionService.stop(context)
    }

    fun incrementStep() {
        incrementStepUseCase()
    }

    fun selectKeyword(keyword: String) {
        viewModelScope.launch {
            selectQuestUseCase(keyword)
        }
    }

    private fun PlayState.toIntState(): Int = when (this) {
        PlayState.STOPPED -> QUEST_STOP
        PlayState.PLAYING -> QUEST_START
        PlayState.SUCCESS -> QUEST_SUCCESS
    }

    private fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }

    private fun formatDistance(meters: Float): String {
        return if (meters >= 1000) {
            String.format("%.1fkm", meters / 1000)
        } else {
            String.format("%.0fm", meters)
        }
    }
}
