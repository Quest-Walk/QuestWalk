package com.hapataka.questwalk.domain.entity

const val RESULT_TYPE = "result"
const val ACHIEVE_TYPE = "achievement"
sealed class HistoryEntity(open val registerAt: String = "") {
    data class ResultEntity(
        override val registerAt: String = "",
        val quest: String = "",
        val time: Long = 0,
        val distance: Float = 0f,
        val step: Int = 0,
        @JvmField
        val isFailed: Boolean = false,
        val locations: List<Pair<Float,Float>>?,
        val questLocation: Pair<Float, Float>? = null,
        val questImg: String? = null,
        val type: String = RESULT_TYPE
    ) : HistoryEntity(registerAt)

    data class AchievementEntity(
        override val registerAt: String  = "",
        val achievementId: Int = -1,
        val type: String = ACHIEVE_TYPE
    ) : HistoryEntity(registerAt)
}
