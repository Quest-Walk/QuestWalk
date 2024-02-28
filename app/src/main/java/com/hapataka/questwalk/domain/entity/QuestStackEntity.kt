package com.hapataka.questwalk.domain.entity

data class QuestStackEntity (
    val keyWord: String,
    val level: Int,
    var successItems: List<SuccessItem>
) {
    data class SuccessItem(
        val userId: String,
        val imageUrl: String
    )
}
