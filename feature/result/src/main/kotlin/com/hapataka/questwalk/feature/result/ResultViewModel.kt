package com.hapataka.questwalk.feature.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetQuestByKeywordUseCase
import com.hapataka.questwalk.core.domain.usecase.GetQuestResultUseCase
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.model.Location
import com.hapataka.questwalk.core.model.Quest
import com.hapataka.questwalk.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getQuestResultUseCase: GetQuestResultUseCase,
    private val getQuestByKeywordUseCase: GetQuestByKeywordUseCase,
) : ViewModel() {

    private val resultId: String = savedStateHandle.get<String>("resultId") ?: ""

    private val _uiState = MutableStateFlow<UiState<ResultUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadResult()
    }

    private fun loadResult() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }

            val questResult = getQuestResultUseCase(resultId).getOrElse { error ->
                _uiState.update { UiState.Failure(error) }
                return@launch
            }

            val quest = getQuestByKeywordUseCase(questResult.questKeyword).getOrNull()

            _uiState.update {
                UiState.Success(questResult.toUiState(quest))
            }
        }
    }

    private fun History.QuestResult.toUiState(quest: Quest?): ResultUiState {
        val otherImages = quest?.successItems
            ?.map { it.imageUrl }
            ?.filter { it != imageUrl }
            ?.takeLast(4)
            ?: emptyList()

        return ResultUiState(
            keyword = questKeyword,
            imageUrl = imageUrl ?: "",
            duration = duration,
            distance = distance,
            step = step,
            route = route,
            successLocation = successLocation,
            successCount = quest?.successItems?.size ?: 0,
            otherImages = otherImages,
        )
    }
}

data class ResultUiState(
    val keyword: String = "",
    val imageUrl: String = "",
    val duration: Long = 0L,
    val distance: Float = 0f,
    val step: Long = 0L,
    val route: List<Location> = emptyList(),
    val successLocation: Location? = null,
    val successCount: Int = 0,
    val otherImages: List<String> = emptyList(),
)
