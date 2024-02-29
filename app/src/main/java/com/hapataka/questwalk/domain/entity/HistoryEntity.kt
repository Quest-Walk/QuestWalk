package com.hapataka.questwalk.domain.entity

const val RESULT_TYPE = "result"
const val ACHIEVE_TYPE = "achievement"
sealed class HistoryEntity(open val registerAt: String = "") {
    data class ResultEntity(
        override val registerAt: String = "",
        val quest: String = "",
        val time: String = "",
        val distance: Float = 0f,
        val step: Int = 0,
        @JvmField
        val isFailed: Boolean = false,
        val longitudes: List<Float> = listOf(),
        val latitueds: List<Float> = listOf(),
        val questLongitude: Float = 0f,
        val questLatitued: Float = 0f,
        val questImg: String? = null,
        val type: String = RESULT_TYPE
    ) : HistoryEntity(registerAt)

    data class AchievementEntity(
        override val registerAt: String  = "",
        val achievementId: Int = -1,
        val type: String = ACHIEVE_TYPE
    ) : HistoryEntity(registerAt)
}
