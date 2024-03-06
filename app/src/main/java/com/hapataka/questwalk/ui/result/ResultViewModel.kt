package com.hapataka.questwalk.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.ui.quest.QuestData
import kotlinx.coroutines.launch

class ResultViewModel: ViewModel() {
    private val userRepositoryImpl = UserRepositoryImpl()
    private val questRepositoryImpl = QuestStackRepositoryImpl()
    private val _resultItem = MutableLiveData<HistoryEntity.ResultEntity>()
    val resultItem: LiveData<HistoryEntity.ResultEntity> = _resultItem
    private val _questItem = MutableLiveData<QuestData>()
    val questItem: LiveData<QuestData> = _questItem

    fun getResultHistory(userId: String, imageUrl: String) {
        viewModelScope.launch {
            val userResults = userRepositoryImpl.getResultHistory(userId)
            _resultItem.value = userResults.first {
                it.questImg == imageUrl
            }
        }
    }

    fun getQuestByKeyword(keyWord: String) {
        viewModelScope.launch {
//            _questItem.value = questRepositoryImpl.getItemByKeyword(keyWord)
            _questItem.value = convertToQuestData(questRepositoryImpl.getItemByKeyword(keyWord))
        }
    }

    private fun convertToQuestData(questStackEntity: QuestStackEntity):QuestData {
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