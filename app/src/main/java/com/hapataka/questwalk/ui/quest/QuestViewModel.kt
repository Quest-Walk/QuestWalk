package com.hapataka.questwalk.ui.quest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import kotlinx.coroutines.launch

class QuestViewModel : ViewModel() {
    private val questStackRepositoryImpl = QuestStackRepositoryImpl()
    private val userRepositoryImpl = UserRepositoryImpl()
    private val authRepositoryImpl = AuthRepositoryImpl()
    private var allQuestItems : MutableList<QuestData>? = null
    private var currentLevel = 0
    private val _questItems = MutableLiveData<MutableList<QuestData>>()
    val questItems: LiveData<MutableList<QuestData>> = _questItems

    init {
        getQuestItems()
    }

    private fun getQuestItems() {
        viewModelScope.launch {
            _questItems.value = questStackRepositoryImpl.getAllItems().map {
                convertToQuestData(it)
            }.toMutableList()
            allQuestItems = _questItems.value?.toMutableList() ?: mutableListOf()
        }
    }

    fun filterLevel(level: Int) {
        currentLevel = level
        if (currentLevel == 0) {
            _questItems.value = allQuestItems ?: mutableListOf()
            return
        }
        val filterList = allQuestItems?.filter { it.level == level }
        _questItems.value = filterList?.toMutableList()
    }

    fun filterComplete(isChecked: Boolean) {
        if (isChecked) {
            // filterComplet
            // 받아온 complete로 filter된 리스트를 allQuestItems에 저장하고 저장된 level로 필터
            viewModelScope.launch {
                val uid = authRepositoryImpl.getCurrentUserUid()
                val completeKeywords = userRepositoryImpl.getResultHistory(uid).map { it.quest }

                val filterList = allQuestItems?.filter { !completeKeywords.contains(it.keyWord) }
                allQuestItems = filterList?.toMutableList()
                
            }
        } else {
            // getQuestItems()를 호출해서 allQuestItems를 전체 아이템으로 다시 세팅하고 저장된 level로 필터
            getQuestItems()
            filterLevel(currentLevel)
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