package com.hapataka.questwalk.core.dataapi.model

data class QuestDto(
    val keyword: String = "",
    val level: Int = 0,
    val successItems: List<SuccessItemDto> = emptyList(),
)

data class SuccessItemDto(
    val userId: String = "",
    val imageUrl: String = "",
    val registerAt: String = "",
)
