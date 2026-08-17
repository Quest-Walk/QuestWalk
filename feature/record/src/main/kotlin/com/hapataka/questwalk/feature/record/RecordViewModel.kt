package com.hapataka.questwalk.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetAchieveItemsUseCase
import com.hapataka.questwalk.core.domain.usecase.GetLoginUserIdUseCase
import com.hapataka.questwalk.core.domain.usecase.GetUserHistoriesUseCase
import com.hapataka.questwalk.core.model.AchieveItem
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val getLoginUserIdUseCase: GetLoginUserIdUseCase,
    private val getUserHistoriesUseCase: GetUserHistoriesUseCase,
    private val getAchieveItemsUseCase: GetAchieveItemsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<RecordUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<RecordEvent>()
    val event = _event.asSharedFlow()

    init {
        loadRecords()
    }

    fun onIntent(intent: RecordIntent) {
        _uiState.update { state -> reduce(state, intent) }
        handleSideEffect(intent)
    }

    private fun reduce(
        state: UiState<RecordUiState>,
        intent: RecordIntent,
    ): UiState<RecordUiState> {
        return when (intent) {
            RecordIntent.Refresh,
            is RecordIntent.ClickHistory,
            -> state

            is RecordIntent.SelectTab -> {
                if (state is UiState.Success) {
                    UiState.Success(state.data.copy(selectedTab = intent.tab))
                } else {
                    state
                }
            }
        }
    }

    private fun handleSideEffect(intent: RecordIntent) {
        when (intent) {
            RecordIntent.Refresh -> loadRecords()
            is RecordIntent.SelectTab -> Unit
            is RecordIntent.ClickHistory -> {
                viewModelScope.launch {
                    _event.emit(RecordEvent.NavigateToResult(intent.historyId))
                }
            }
        }
    }

    private fun loadRecords() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }

            val userId = getLoginUserIdUseCase().getOrElse { error ->
                _uiState.update { UiState.Failure(error) }
                return@launch
            }

            val historiesDeferred = async { getUserHistoriesUseCase(userId) }
            val achieveItemsDeferred = async { getAchieveItemsUseCase() }

            val histories = historiesDeferred.await().getOrElse { error ->
                _uiState.update { UiState.Failure(error) }
                return@launch
            }

            val achieveItems = achieveItemsDeferred.await().getOrElse { error ->
                _uiState.update { UiState.Failure(error) }
                return@launch
            }

            val achievedIds = histories
                .filterIsInstance<History.Achievement>()
                .map { it.achievementId }
                .toSet()

            _uiState.update {
                UiState.Success(
                    RecordUiState(
                        histories = histories,
                        achieveItems = achieveItems.map { item ->
                            item.toUiModel(isAchieved = item.achieveId in achievedIds)
                        }
                    )
                )
            }
        }
    }

    private fun AchieveItem.toUiModel(isAchieved: Boolean): AchieveItemUiModel {
        return AchieveItemUiModel(
            achieveId = achieveId,
            achieveIcon = achieveIcon,
            achieveTitle = achieveTitle,
            achieveDescription = achieveDescription,
            isAchieved = isAchieved
        )
    }
}

data class RecordUiState(
    val histories: List<History> = emptyList(),
    val achieveItems: List<AchieveItemUiModel> = emptyList(),
    val selectedTab: RecordTab = RecordTab.HISTORY,
)

data class AchieveItemUiModel(
    val achieveId: Int,
    val achieveIcon: String,
    val achieveTitle: String,
    val achieveDescription: String,
    val isAchieved: Boolean = false,
)

enum class RecordTab {
    HISTORY, ACHIEVEMENT
}

sealed interface RecordIntent {
    data object Refresh : RecordIntent
    data class SelectTab(val tab: RecordTab) : RecordIntent
    data class ClickHistory(val historyId: String) : RecordIntent
}

sealed interface RecordEvent {
    data class NavigateToResult(val historyId: String) : RecordEvent
}
