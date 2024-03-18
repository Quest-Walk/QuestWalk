package com.hapataka.questwalk.domain.entity

data class AchieveItemEntity (
    val achieveId: Int = -1,
    val achieveIcon: String = "",
    val achieveTitle: String = "",
    val achieveDescription: String = "",
    val isHidden: Boolean = false
)