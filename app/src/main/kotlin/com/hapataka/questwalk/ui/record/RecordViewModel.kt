package com.hapataka.questwalk.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.ui.UiState
import com.hapataka.questwalk.domain.entity.AchieveItemEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.AchieveResultEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.ResultEntity
import com.hapataka.questwalk.domain.facade.HistoryFacade
import com.hapataka.questwalk.domain.repository.AchieveItemRepository
import com.hapataka.questwalk.domain.repository.UserRepo
import com.hapataka.questwalk.ui.record.model.RecordItem
import com.hapataka.questwalk.ui.record.model.RecordItem.AchieveItem
import com.hapataka.questwalk.ui.record.model.RecordItem.ResultItem
import com.hapataka.questwalk.util.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val achieveItemRepo: AchieveItemRepository,
    private val historyFacade: HistoryFacade,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<RecordUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadRecords()
    }

    fun onAction(action: RecordAction) {
        when (action) {
            is RecordAction.Refresh -> loadRecords()
            is RecordAction.ClickHistory -> { /* Navigation handled in Route */ }
        }
    }

    private fun loadRecords() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }

            try {
                val histories = historyFacade.getCurrentUserHistories() ?: emptyList()
                val achieveItems = loadAchieveItems()

                _uiState.update {
                    UiState.Success(
                        RecordUiState(
                            histories = histories,
                            achieveItems = achieveItems
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update { UiState.Failure(e) }
            }
        }
    }

    private suspend fun loadAchieveItems(): List<AchieveItem> {
        val histories = userRepo.getUserHistory(UserInfo.uid)
        return achieveItemRepo.getAchieveItem().map { entity ->
            convertToRecordItem(entity, histories.filterIsInstance<AchieveResultEntity>())
        }
    }

    private fun convertToRecordItem(
        entity: AchieveItemEntity,
        history: List<AchieveResultEntity>,
    ): AchieveItem {
        return with(entity) {
            AchieveItem(
                achieveId,
                achieveIcon,
                achieveTitle,
                achieveDescription,
                history.any { it.achievementId == entity.achieveId }
            )
        }
    }
}

data class RecordUiState(
    val histories: List<History> = emptyList(),
    val achieveItems: List<AchieveItem> = emptyList(),
)

sealed interface RecordAction {
    data object Refresh : RecordAction
    data class ClickHistory(val historyId: String) : RecordAction
}