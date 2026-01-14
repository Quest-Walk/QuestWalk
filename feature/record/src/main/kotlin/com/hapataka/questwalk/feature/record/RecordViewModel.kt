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
import kotlinx.coroutines.flow.MutableStateFlow
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

    init {
        loadRecords()
    }

    fun onAction(action: RecordAction) {
        when (action) {
            is RecordAction.Refresh -> loadRecords()
            is RecordAction.SelectTab -> selectTab(action.tab)
            is RecordAction.ClickHistory -> { /* Navigation handled in Route */ }
        }
    }

    private fun loadRecords() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }

            val userId = getLoginUserIdUseCase().getOrElse {
                _uiState.update { UiState.Failure(it) }
                return@launch
            }

            val historiesDeferred = async { getUserHistoriesUseCase(userId) }
            val achieveItemsDeferred = async { getAchieveItemsUseCase() }

            val histories = historiesDeferred.await().getOrElse {
                _uiState.update { UiState.Failure(it) }
                return@launch
            }

            val achieveItems = achieveItemsDeferred.await().getOrElse {
                _uiState.update { UiState.Failure(it) }
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

    private fun selectTab(tab: RecordTab) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(currentState.copy(selectedTab = tab))
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

sealed interface RecordAction {
    data object Refresh : RecordAction
    data class SelectTab(val tab: RecordTab) : RecordAction
    data class ClickHistory(val historyId: String) : RecordAction
}
