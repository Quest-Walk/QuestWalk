package com.hapataka.questwalk.feature.quest.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.repository.QuestRepository
import com.hapataka.questwalk.core.model.Quest
import com.hapataka.questwalk.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class QuestDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val questRepository: QuestRepository,
) : ViewModel() {

    private val keyword: String = savedStateHandle.get<String>("keyword") ?: ""

    private val _uiState = MutableStateFlow<UiState<QuestDetailUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadQuestDetail()
    }

    fun onAction(action: QuestDetailAction) {
        when (action) {
            is QuestDetailAction.Refresh -> loadQuestDetail()
            is QuestDetailAction.ClickImage -> { /* Navigation handled in Route */ }
        }
    }

    private fun loadQuestDetail() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }

            questRepository.getQuestByKeyword(keyword)
                .onSuccess { quest ->
                    _uiState.update {
                        UiState.Success(quest.toUiState())
                    }
                }
                .onFailure { error ->
                    _uiState.update { UiState.Failure(error) }
                }
        }
    }

    private fun Quest.toUiState(): QuestDetailUiState {
        val totalUsers = successItems.size.toLong()
        val completeRate = if (totalUsers > 0) {
            ((totalUsers.toDouble() / 100) * 1000).roundToInt() / 10.0
        } else {
            0.0
        }

        return QuestDetailUiState(
            keyword = keyword,
            level = level,
            successCount = successItems.size,
            completeRate = completeRate,
            successImages = successItems.map { item ->
                SuccessImageUiModel(
                    userId = item.userId,
                    imageUrl = item.imageUrl,
                    registerAt = item.registerAt,
                )
            }.reversed()
        )
    }
}

data class QuestDetailUiState(
    val keyword: String = "",
    val level: Int = 0,
    val successCount: Int = 0,
    val completeRate: Double = 0.0,
    val successImages: List<SuccessImageUiModel> = emptyList(),
)

data class SuccessImageUiModel(
    val userId: String,
    val imageUrl: String,
    val registerAt: String,
)

sealed interface QuestDetailAction {
    data object Refresh : QuestDetailAction
    data class ClickImage(val imageUrl: String) : QuestDetailAction
}
