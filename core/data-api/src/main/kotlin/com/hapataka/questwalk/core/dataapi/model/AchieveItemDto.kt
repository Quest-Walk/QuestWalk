package com.hapataka.questwalk.core.dataapi.model

data class AchieveItemDto(
    val achieveId: Int = -1,
    val achieveIcon: String = "",
    val achieveTitle: String = "",
    val achieveDescription: String = "",
    val isHidden: Boolean = false,
)
