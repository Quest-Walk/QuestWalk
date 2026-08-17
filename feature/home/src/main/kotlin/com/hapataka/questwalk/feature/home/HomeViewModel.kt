package com.hapataka.questwalk.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.FinalizeQuestUseCase
import com.hapataka.questwalk.core.domain.usecase.GetCurrentQuestUseCase
import com.hapataka.questwalk.core.domain.usecase.GetPlaySessionUseCase
import com.hapataka.questwalk.core.domain.usecase.IncrementStepUseCase
import com.hapataka.questwalk.core.domain.usecase.ResetPlaySessionUseCase
import com.hapataka.questwalk.core.domain.usecase.SelectRandomQuestUseCase
import com.hapataka.questwalk.core.domain.usecase.StartPlaySessionUseCase
import com.hapataka.questwalk.core.domain.usecase.StopPlaySessionUseCase
import com.hapataka.questwalk.core.service.PlaySessionServiceController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val getPlaySessionUseCase: GetPlaySessionUseCase,
    private val startPlaySessionUseCase: StartPlaySessionUseCase,
    private val stopPlaySessionUseCase: StopPlaySessionUseCase,
    private val incrementStepUseCase: IncrementStepUseCase,
    private val resetPlaySessionUseCase: ResetPlaySessionUseCase,
    private val finalizeQuestUseCase: FinalizeQuestUseCase,
    private val serviceController: PlaySessionServiceController,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()

    init {
        initializeQuestIfNeeded()
        observeState()
        updateCurrentTime()
    }

    fun onIntent(intent: HomeIntent) {
        _uiState.update { state -> reduce(state, intent) }
        handleSideEffect(intent)
    }

    private fun reduce(state: HomeUiState, intent: HomeIntent): HomeUiState {
        return when (intent) {
            HomeIntent.StartClicked,
            HomeIntent.CompleteClicked,
            HomeIntent.CameraClicked,
            HomeIntent.QuestChangeClicked,
            HomeIntent.WeatherClicked,
            HomeIntent.MyInfoClicked,
            HomeIntent.RecordClicked,
            HomeIntent.StepDetected,
            -> state

            HomeIntent.StopClicked -> state.copy(showStopConfirmDialog = true)
            HomeIntent.StopConfirmed -> state.copy(showStopConfirmDialog = false)
            HomeIntent.StopDismissed -> state.copy(showStopConfirmDialog = false)
        }
    }

    private fun handleSideEffect(intent: HomeIntent) {
        when (intent) {
            HomeIntent.StartClicked -> startSession()
            HomeIntent.StopClicked,
            HomeIntent.StopDismissed,
            -> Unit

            HomeIntent.StopConfirmed -> stopSession()
            HomeIntent.CompleteClicked -> completeQuest()
            HomeIntent.CameraClicked -> emitEvent(HomeEvent.NavigateToCamera)
            HomeIntent.QuestChangeClicked -> emitEvent(HomeEvent.NavigateToQuest)
            HomeIntent.WeatherClicked -> emitEvent(HomeEvent.NavigateToWeather)
            HomeIntent.MyInfoClicked -> emitEvent(HomeEvent.NavigateToMyInfo)
            HomeIntent.RecordClicked -> emitEvent(HomeEvent.NavigateToRecord)
            HomeIntent.StepDetected -> incrementStepUseCase()
        }
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
                    playState = session.playState,
                    duration = formatDuration(session.duration),
                    step = session.steps.toString(),
                    distance = formatDistance(session.distance),
                    rawDistance = session.distance,
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

    private fun startSession() {
        runCatching { startPlaySessionUseCase() }
            .onSuccess {
                serviceController.start()
            }
            .onFailure { error ->
                emitEvent(HomeEvent.ShowError(error.message ?: "세션 시작에 실패했습니다"))
            }
    }

    private fun stopSession() {
        stopPlaySessionUseCase()
        serviceController.stop()
    }

    private fun completeQuest() {
        if (_uiState.value.isCompleting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isCompleting = true) }
            try {
                stopPlaySessionUseCase()
                serviceController.stop()
                val resultId = finalizeQuestUseCase()
                resetPlaySessionUseCase()
                selectRandomQuestUseCase()
                _event.emit(HomeEvent.NavigateToResult(resultId))
            } catch (error: Exception) {
                _event.emit(HomeEvent.ShowError(error.message ?: "퀘스트 완료에 실패했습니다"))
            } finally {
                _uiState.update { it.copy(isCompleting = false) }
            }
        }
    }

    private fun emitEvent(event: HomeEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
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

sealed interface HomeIntent {
    data object StartClicked : HomeIntent
    data object StopClicked : HomeIntent
    data object StopConfirmed : HomeIntent
    data object StopDismissed : HomeIntent
    data object CompleteClicked : HomeIntent
    data object CameraClicked : HomeIntent
    data object QuestChangeClicked : HomeIntent
    data object WeatherClicked : HomeIntent
    data object MyInfoClicked : HomeIntent
    data object RecordClicked : HomeIntent
    data object StepDetected : HomeIntent
}

sealed interface HomeEvent {
    data object NavigateToCamera : HomeEvent
    data class NavigateToResult(val resultId: String) : HomeEvent
    data object NavigateToQuest : HomeEvent
    data object NavigateToWeather : HomeEvent
    data object NavigateToMyInfo : HomeEvent
    data object NavigateToRecord : HomeEvent
    data class ShowError(val message: String) : HomeEvent
}
