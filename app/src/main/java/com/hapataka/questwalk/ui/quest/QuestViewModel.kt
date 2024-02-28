package com.hapataka.questwalk.ui.quest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuestViewModel : ViewModel() {
    var allQuestItems = mutableListOf<QuestStatsEntity>()
    var currentLevel = 0
    private val _questItems = MutableLiveData<MutableList<QuestStatsEntity>>()
    val questItems: LiveData<MutableList<QuestStatsEntity>> = _questItems

    init {
        getQuestItems()
    }

    private fun getQuestItems() {
        // questItem을 받아오는 코드 & allQuestItems에 전체 아이템 세팅
    }
    fun filterLevel(level: Int) {
        currentLevel = level
        val filterList = allQuestItems.filter { it.level == level }
        _questItems.value = filterList.toMutableList()
    }

    fun filterComplete(isChecked: Boolean) {
        if (isChecked) {
            // filterComplet
            // 받아온 complete로 filter된 리스트를 allQuestItems에 저장하고 저장된 level로 필터
        } else {
            // getQuestItems()를 호출해서 allQuestItems를 전체 아이템으로 다시 세팅하고 저장된 level로 필터
        }
    }
}