package com.hapataka.questwalk.data.resource

data class Achievement (
    val achieveId: Int,
    val achieveIcon: Int,
    val achieveTitle: String,
    val achieveDescription: String,
    val isHidden: Boolean
)