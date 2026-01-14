package com.hapataka.questwalk.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetQuestResultUseCase
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.domain.repository.QuestStackRepository
import com.hapataka.questwalk.domain.repository.UserRepo
import com.hapataka.questwalk.ui.quest.QuestData
import com.hapataka.questwalk.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val questRepo: QuestStackRepository,
    private val getQuestResultUseCase: GetQuestResultUseCase,
) : ViewModel() {
    private val _questResult = MutableStateFlow<UiState<History.QuestResult>>(UiState.Idle)
    val questResult = _questResult.asLiveData()

    private val _questItem = MutableLiveData<QuestData>()
    val questItem: LiveData<QuestData> = _questItem
    private val _completeRate = MutableLiveData<Double>()
    val completeRate: LiveData<Double> get() = _completeRate

    fun getResult(resultId: String) {
        viewModelScope.launch {
            _questResult.update { UiState.Loading }

            getQuestResultUseCase(resultId)
                .onSuccess { item ->
                    _questResult.update { UiState.Success(item) }

                }
                .onFailure { e ->
                    _questResult.update { UiState.Failure(e) }
                }

            if (questResult.value is UiState.Success) {
                getQuestByKeyword(
                    (questResult.value as UiState.Success<History.QuestResult>)?.data?.questKeyword
                        ?: ""
                )
            }
        }
    }

    private fun getQuestByKeyword(keyWord: String) {
        viewModelScope.launch {
            val allUser = userRepo.getAllUserSize()
            val questItem = convertToQuestData(questRepo.getItemByKeyword(keyWord))
            _questItem.value = questItem
            _completeRate.value = round((questItem.successItems.size.toDouble() / allUser) * 100)
        }
    }

    private fun convertToQuestData(questStackEntity: QuestStackEntity): QuestData {
        val resultItems = questStackEntity.successItems.map {
            QuestData.SuccessItem(it.userId, it.imageUrl)
        }
        return QuestData(
            keyWord = questStackEntity.keyWord,
            level = questStackEntity.level,
            successItems = resultItems
        )
    }
}