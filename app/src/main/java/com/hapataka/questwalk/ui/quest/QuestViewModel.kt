package com.hapataka.questwalk.ui.quest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.domain.usecase.QuestFilteringUseCase
import kotlinx.coroutines.launch

class QuestViewModel : ViewModel() {
    private val questStackRepositoryImpl = QuestStackRepositoryImpl()
    private val userRepositoryImpl = UserRepositoryImpl()
    private val authRepositoryImpl = AuthRepositoryImpl()
    private var allQuestItems: MutableList<QuestData>? = null
    private var currentLevel = 0
    private val _questItems = MutableLiveData<MutableList<QuestData>>()
    val questItems: LiveData<MutableList<QuestData>> = _questItems
    private val _allUserSize = MutableLiveData<Long>()
    val allUserSize: LiveData<Long> = _allUserSize
    val filterUseCase = QuestFilteringUseCase()

    init {
        getQuestItems(false)
        getAllUserSize()
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
                val filterList = mutableListOf<QuestData>()

                filterUseCase().forEach {
                    filterList += convertToQuestData(it)
                }
                allQuestItems = filterList?.toMutableList()
                filterLevel(currentLevel)

            }
        } else {
            getQuestItems(true)
        }
    }

    private fun getAllUserSize() {
        viewModelScope.launch {
            _allUserSize.value = userRepositoryImpl.getAllUserSize()
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