package com.hapataka.questwalk.core.model

data class AchieveItem(
    val achieveId: Int,
    val achieveIcon: String,
    val achieveTitle: String,
    val achieveDescription: String,
    val isHidden: Boolean = false,
)
