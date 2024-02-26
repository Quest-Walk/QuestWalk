package com.hapataka.questwalk.domain.entity

sealed class History {
    open val registerAt: String = ""
}

data class ResultEntity(
    override val registerAt: String,
    val quest: String,
    val time: String,
    val distance: Float,
    val isFailed: Boolean,
    val longitudes: List<Float>,
    val latitueds: List<Float>,
    val questLongitude: Float = 0f,
    val questLatitued: Float = 0f,
    val questImg: String? = null
): History()

data class AchievementEntity(
    override val registerAt: String,
    val achievementId: Int
): History()