package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.domain.entity.QuestStatsEntity

interface QuestStatRepository {
    fun updateQuest(quest: QuestStatsEntity)
    fun loadAllQuests()
    fun loadQuest(keyword: String)
}