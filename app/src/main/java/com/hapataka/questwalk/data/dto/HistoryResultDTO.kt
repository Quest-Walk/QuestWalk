package com.hapataka.questwalk.data.dto

const val RESULT_RECORD = 1
const val ACHIEVEMENT_RECORD = 2

data class HistoryResultDTO (
    val resultRecords: List<ResultRecordDTO> = emptyList(),
    val achievementRecords: List<AchievementRecordDTO> = emptyList()
)

data class ResultRecordDTO(
    val userId: String = "",
    val registerAt: String = "",
    val questKeyword: String = "",
    val duration: Long = 0L,
    val distance: Float = 0f,
    val step: Long = 0L,
    @JvmField
    val isSuccess: Boolean = false,
    val route: String = "",
    val successLocation: String? = null,
    val questImg: String? = null,
    val recordType: Int = RESULT_RECORD
)
data class AchievementRecordDTO(
    val userId: String = "",
    val registerAt: String = "",
    val achievementId: Int = 0,
    val recordType: Int = ACHIEVEMENT_RECORD
)
