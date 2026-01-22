package com.hapataka.questwalk.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetAvailableQuestsUseCase
import com.hapataka.questwalk.core.domain.usecase.GetPlaySessionUseCase
import com.hapataka.questwalk.core.domain.usecase.IncrementStepUseCase
import com.hapataka.questwalk.core.domain.usecase.ResetPlaySessionUseCase
import com.hapataka.questwalk.core.domain.usecase.StartPlaySessionUseCase
import com.hapataka.questwalk.core.domain.usecase.StopPlaySessionUseCase
import com.hapataka.questwalk.core.model.PlayState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPlaySessionUseCase: GetPlaySessionUseCase,
    private val startPlaySessionUseCase: StartPlaySessionUseCase,
    private val stopPlaySessionUseCase: StopPlaySessionUseCase,
    private val incrementStepUseCase: IncrementStepUseCase,
    private val resetPlaySessionUseCase: ResetPlaySessionUseCase,
    private val getAvailableQuestsUseCase: GetAvailableQuestsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observePlaySession()
        updateCurrentTime()
        loadRandomKeyword()
    }

    private fun observePlaySession() {
        viewModelScope.launch {
            getPlaySessionUseCase().collect { session ->
                _uiState.update {
                    it.copy(
                        currentKeyword = session.keyword.ifEmpty { it.currentKeyword },
                        keywordLevel = if (session.level > 0) session.level else it.keywordLevel,
                        playState = session.playState.toIntState(),
                        duration = formatDuration(session.duration),
                        step = session.steps.toString(),
                        distance = formatDistance(session.distance),
                    )
                }
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

    private fun loadRandomKeyword() {
        viewModelScope.launch {
            getAvailableQuestsUseCase()
                .onSuccess { quests ->
                    if (quests.isNotEmpty()) {
                        val quest = quests.random()
                        _uiState.update {
                            it.copy(
                                currentKeyword = quest.keyword,
                                keywordLevel = quest.level,
                            )
                        }
                    }
                }
        }
    }

    fun startSession() {
        val keyword = _uiState.value.currentKeyword
        val level = _uiState.value.keywordLevel
        if (keyword.isNotEmpty()) {
            startPlaySessionUseCase(keyword, level)
        }
    }

    fun stopSession() {
        stopPlaySessionUseCase()
    }

    fun resetSession() {
        resetPlaySessionUseCase()
        loadRandomKeyword()
    }

    fun incrementStep() {
        incrementStepUseCase()
    }

    fun selectKeyword(keyword: String) {
        viewModelScope.launch {
            getAvailableQuestsUseCase()
                .onSuccess { quests ->
                    val quest = quests.find { it.keyword == keyword }
                    if (quest != null) {
                        _uiState.update {
                            it.copy(
                                currentKeyword = quest.keyword,
                                keywordLevel = quest.level,
                            )
                        }
                    }
                }
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
