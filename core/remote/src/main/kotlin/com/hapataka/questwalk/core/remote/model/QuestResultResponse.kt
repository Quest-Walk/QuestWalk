package com.hapataka.questwalk.core.remote.model

import androidx.annotation.Keep

/**
 * Firebase Firestore 응답 모델 (histories 컬렉션)
 * Firebase 필드명과 일치해야 함
 */
@Keep
data class QuestResultResponse(
    val userId: String = "",
    val registerAt: String = "",
    val questKeyword: String = "",
    val duration: Long = 0L,
    val distance: Float = 0f,
    val step: Long = 0L,
    @field:JvmField
    val isSuccess: Boolean = false,
    val route: String = "",
    val successLocation: String? = null,
    val questImg: String? = null,
    val recordType: Int = RECORD_TYPE_QUEST,
) {
    companion object {
        const val RECORD_TYPE_QUEST = 1
    }
}