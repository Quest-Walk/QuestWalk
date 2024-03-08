package com.hapataka.questwalk.domain.entity

data class QuestStackEntity (
    val keyWord: String = "",
    val level: Int = 0,
    var successItems: List<SuccessItem> = listOf()
) {
    data class SuccessItem(
        val userId: String = "",
        val imageUrl: String = "",
        val registerAt: String = ""
    )
}
