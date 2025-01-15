package com.hapataka.questwalk.core.remote.model

data class QuestResultDto(
    val resultId: String = "",
    val userId: String = "",
    val registerAt: String = "",
    val questKeyword: String = "",
    val duration: Long = 0L,
    val distance: Float = 0f,
    val step: Long = 0L,
    val isSuccess: Boolean = false,
    val route: String = "",
    val successLocation: String? = null,
    val imageUrl: String? = null,
    val type: Int = 1,
)