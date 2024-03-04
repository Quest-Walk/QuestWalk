package com.hapataka.questwalk.ui.quest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import kotlinx.coroutines.Job
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
        getQuestItems(false)
    }

    private fun getQuestItems(completeFilter: Boolean) {
        viewModelScope.launch {
            _questItems.value = questStackRepositoryImpl.getAllItems().map {
                convertToQuestData(it)
            }.toMutableList()
            allQuestItems = _questItems.value?.toMutableList() ?: mutableListOf()
            if (completeFilter) filterLevel(currentLevel)
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
            viewModelScope.launch {
//                val uid = authRepositoryImpl.getCurrentUserUid()
                val testUid = "8fEEPVnXYjPMyXIeoWTxelYc9qo1"
                val completeKeywords = userRepositoryImpl.getResultHistory(testUid).map { it.quest }

                val filterList = allQuestItems?.filter { !completeKeywords.contains(it.keyWord) }
                allQuestItems = filterList?.toMutableList()
                filterLevel(currentLevel)
                
            }
        } else {
            getQuestItems(true)
//            filterLevel(currentLevel)
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