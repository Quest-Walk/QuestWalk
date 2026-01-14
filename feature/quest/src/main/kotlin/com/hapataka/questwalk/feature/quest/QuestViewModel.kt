package com.hapataka.questwalk.feature.quest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetAllQuestsUseCase
import com.hapataka.questwalk.core.domain.usecase.GetSuccessKeywordsUseCase
import com.hapataka.questwalk.core.model.Quest
import com.hapataka.questwalk.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class QuestViewModel @Inject constructor(
    private val getAllQuestsUseCase: GetAllQuestsUseCase,
    private val getSuccessKeywordsUseCase: GetSuccessKeywordsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<QuestUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var allQuests: List<Quest> = emptyList()
    private var successKeywords: Set<String> = emptySet()
    private var allUserCount: Long = 0L

    init {
        loadQuests()
    }

    fun onAction(action: QuestAction) {
        when (action) {
            is QuestAction.Refresh -> loadQuests()
            is QuestAction.FilterLevel -> filterByLevel(action.level)
            is QuestAction.SelectQuest -> { /* Navigation handled in Route */ }
            is QuestAction.ShowQuestDetail -> { /* Navigation handled in Route */ }
        }
    }

    private fun loadQuests() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }

            val questsDeferred = async { getAllQuestsUseCase() }
            val keywordsDeferred = async { getSuccessKeywordsUseCase() }

            val quests = questsDeferred.await().getOrElse { error ->
                _uiState.update { UiState.Failure(error) }
                return@launch
            }

            successKeywords = keywordsDeferred.await().getOrElse { emptySet() }

            allQuests = quests
            // 전체 유저 수는 별도로 가져와야 하지만, 현재 구현에서는 successItems 기반으로 계산
            allUserCount = quests.maxOfOrNull { it.successItems.size.toLong() } ?: 0L

            updateUiState(selectedLevel = 0)
        }
    }

    private fun filterByLevel(level: Int) {
        updateUiState(selectedLevel = level)
    }

    private fun updateUiState(selectedLevel: Int) {
        val filteredQuests = if (selectedLevel == 0) {
            allQuests
        } else {
            allQuests.filter { it.level == selectedLevel }
        }

        val questItems = filteredQuests.map { quest ->
            quest.toUiModel(
                isSuccess = quest.keyword in successKeywords,
                allUserCount = allUserCount
            )
        }

        _uiState.update {
            UiState.Success(
                QuestUiState(
                    quests = questItems,
                    selectedLevel = selectedLevel,
                    successKeywords = successKeywords,
                )
            )
        }
    }

    private fun Quest.toUiModel(isSuccess: Boolean, allUserCount: Long): QuestItemUiModel {
        val completeRate = if (allUserCount > 0) {
            ((successItems.size.toDouble() / allUserCount) * 1000).roundToInt() / 10.0
        } else {
            0.0
        }

        return QuestItemUiModel(
            keyword = keyword,
            level = level,
            successCount = successItems.size,
            completeRate = completeRate,
            isSuccess = isSuccess,
        )
    }
}

data class QuestUiState(
    val quests: List<QuestItemUiModel> = emptyList(),
    val selectedLevel: Int = 0,
    val successKeywords: Set<String> = emptySet(),
)

data class QuestItemUiModel(
    val keyword: String,
    val level: Int,
    val successCount: Int,
    val completeRate: Double,
    val isSuccess: Boolean,
)

sealed interface QuestAction {
    data object Refresh : QuestAction
    data class FilterLevel(val level: Int) : QuestAction
    data class SelectQuest(val keyword: String) : QuestAction
    data class ShowQuestDetail(val keyword: String) : QuestAction
}
