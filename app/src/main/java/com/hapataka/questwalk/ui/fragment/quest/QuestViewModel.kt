package com.hapataka.questwalk.ui.fragment.quest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.domain.repository.UserRepository
import com.hapataka.questwalk.domain.usecase.GetAllQuestUseCase
import com.hapataka.questwalk.domain.usecase.QuestFilteringUseCase
import com.hapataka.questwalk.util.UserInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class QuestViewModel(
    private val userRepo: UserRepository,
    private val getAllQuestUseCase: GetAllQuestUseCase
) : ViewModel() {
    private var allUser: Long = 0
    private var allQuestItems: MutableList<QuestData>? = null
    private var currentLevel = 0

    private val _questItems = MutableLiveData<MutableList<QuestData>>()
    val questItems: LiveData<MutableList<QuestData>> = _questItems

    private val _successKeywords = MutableLiveData<MutableList<String>>()
    val successKeywords: LiveData<MutableList<String>> get() = _successKeywords

    init {
        viewModelScope.launch {
            val getAllUserTask = async { getAllUserSize() }
            val getSuccessKeywordsTask = async { getSuccessKeywords() }
            val list = mutableListOf(getAllUserTask, getSuccessKeywordsTask)

            list.awaitAll()
            getQuestItems()
        }
    }


    private suspend fun getQuestItems() {
        getAllQuestUseCase().map {
            convertToQuestData(it)
        }.also {
            allQuestItems = it.toMutableList()
        }
        filterLevel(currentLevel)
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

    private suspend fun getSuccessKeywords() {
        val successResults = userRepo.getResultHistory(UserInfo.uid).filter { it.isSuccess }
        val successKeywords = successResults.map { it.quest }

        _successKeywords.value = successKeywords.toMutableList()
    }

    private suspend fun getAllUserSize() {
        allUser = userRepo.getAllUserSize()
    }

    private fun convertToQuestData(questStackEntity: QuestStackEntity): QuestData {
        val resultItems = questStackEntity.successItems.map {
            QuestData.SuccessItem(it.userId, it.imageUrl, it.registerAt)
        }
        val isSuccess = _successKeywords.value?.any { it.contains(questStackEntity.keyWord) } ?: false

        return QuestData(
            keyWord = questStackEntity.keyWord,
            level = questStackEntity.level,
            successItems = resultItems,
            allUser = allUser,
            isSuccess = isSuccess
        )
    }
}