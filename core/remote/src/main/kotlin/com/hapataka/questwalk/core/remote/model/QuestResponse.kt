package com.hapataka.questwalk.core.remote.model

/**
 * Firebase Firestore 응답 모델
 * Firebase 필드명과 일치해야 함 (keyWord)
 */
data class QuestResponse(
    val keyWord: String = "",
    val level: Int = 0,
    val successItems: List<SuccessItemResponse> = emptyList(),
) {
    data class SuccessItemResponse(
        val userId: String = "",
        val imageUrl: String = "",
        val registerAt: String = "",
    )
}
