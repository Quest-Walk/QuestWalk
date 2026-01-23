package com.hapataka.questwalk.core.remote.model

import androidx.annotation.Keep

/**
 * Firebase Firestore 응답 모델
 * Firebase 필드명과 일치해야 함 (keyWord)
 */
@Keep
data class QuestResponse(
    val keyWord: String = "",
    val level: Int = 0,
    val successItems: List<SuccessItemResponse> = emptyList(),
) {
    @Keep
    data class SuccessItemResponse(
        val userId: String = "",
        val imageUrl: String = "",
        val registerAt: String = "",
    )
}
