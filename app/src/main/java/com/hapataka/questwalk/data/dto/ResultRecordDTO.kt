package com.hapataka.questwalk.data.dto

const val RESULT = 1
const val ACHIEVEMENT = 2
data class ResultRecordDTO(
    val id: String,
    val registerAt: String,
    val questKeyword: String = "",
    val duration: Long = 0L,
    val distance: Float = 0f,
    val step: Long = 0L,
    val isSuccess: Boolean = false,
    val route: List<Pair<Float, Float>>?,
    val successLocation: Pair<Float, Float>? = null,
    val questImg: String? = null,
    val type: Int = RESULT
)
