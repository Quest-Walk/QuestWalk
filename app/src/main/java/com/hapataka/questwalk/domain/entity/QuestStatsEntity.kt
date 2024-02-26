package com.hapataka.questwalk.domain.entity

data class QuestStatsEntity (
    val keyWord: String,
    val level: Int,
    var successItems: Map<String, String>
)