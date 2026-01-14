package com.hapataka.questwalk.core.remote.model

data class QuestDto(
    val keyWord: String = "",
    val level: Int = 0,
    val successItems: List<SuccessItemDto> = emptyList(),
) {
    data class SuccessItemDto(
        val userId: String = "",
        val imageUrl: String = "",
        val registerAt: String = "",
    )
}
