package com.hapataka.questwalk.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.data.map.GoogleMapRepositoryImpl
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.domain.repository.MapRepository
import com.hapataka.questwalk.ui.quest.QuestData
import kotlinx.coroutines.launch
import kotlin.math.round

class ResultViewModel(
    private val userRepo: UserRepositoryImpl,
    private val questRepo: QuestStackRepositoryImpl,
//    private val mapRepo: GoogleMapRepositoryImpl
) : ViewModel() {
    private val _resultItem = MutableLiveData<HistoryEntity.ResultEntity>()
    val resultItem: LiveData<HistoryEntity.ResultEntity> = _resultItem
    private val _questItem = MutableLiveData<QuestData>()
    val questItem: LiveData<QuestData> = _questItem
    private val _completeRate = MutableLiveData<Double>()
    val completeRate: LiveData<Double> = _completeRate

    fun getResult(userId: String, keyword: String, registerAt: String) {
        viewModelScope.launch {
            val userResults = userRepo.getResultHistory(userId)

            _resultItem.value = userResults.find {
                it.quest == keyword && it.registerAt == registerAt
            }
            getQuestByKeyword(keyword)
//            mapRepo.drawPath(_resultItem.value!!)
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