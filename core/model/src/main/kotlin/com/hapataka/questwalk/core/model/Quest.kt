package com.hapataka.questwalk.core.model

data class Quest(
    val keyword: String,
    val level: Int,
    val successItems: List<SuccessItem>,
) {
    data class SuccessItem(
        val userId: String,
        val imageUrl: String,
        val registerAt: String,
    )
}
