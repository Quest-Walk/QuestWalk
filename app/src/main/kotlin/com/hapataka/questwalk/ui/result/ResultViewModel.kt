package com.hapataka.questwalk.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetQuestResultUseCase
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.domain.repository.QuestStackRepository
import com.hapataka.questwalk.domain.repository.UserRepo
import com.hapataka.questwalk.ui.quest.QuestData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val questRepo: QuestStackRepository,
    private val getQuestResultUseCase: GetQuestResultUseCase,
) : ViewModel() {
    private val _resultItem = MutableLiveData<HistoryEntity.ResultEntity>()
    val resultItem: LiveData<HistoryEntity.ResultEntity> = _resultItem
    private val _questItem = MutableLiveData<QuestData>()
    val questItem: LiveData<QuestData> = _questItem
    private val _completeRate = MutableLiveData<Double>()
    val completeRate: LiveData<Double> get() = _completeRate

    fun getResult(resultId: String) {
        viewModelScope.launch {
            val questResult = getQuestResultUseCase(resultId).getOrThrow()

            _resultItem.value = HistoryEntity.ResultEntity(
                registerAt = questResult.registerAt.toString(),
                quest = questResult.questKeyword,
                time = questResult.duration,
                distance = questResult.distance,
                step = questResult.step,
                isSuccess = questResult.isSuccess,
                locations = questResult.route,
                questLocation = questResult.successLocation,
                questImg = questResult.imageUrl,
            )
            getQuestByKeyword(questResult.questKeyword)
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