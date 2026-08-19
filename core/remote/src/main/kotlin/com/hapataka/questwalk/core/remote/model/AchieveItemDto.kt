package com.hapataka.questwalk.core.remote.model

import androidx.annotation.Keep

@Keep
data class AchieveItemDto(
    val achieveId: Int = -1,
    val achieveIcon: String = "",
    val achieveTitle: String = "",
    val achieveDescription: String = "",
    val isHidden: Boolean = false,
)
