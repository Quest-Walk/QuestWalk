package com.hapataka.questwalk.core.remote.model

/**
 * Firebase Firestore 응답 모델 (histories 컬렉션)
 * Firebase 필드명과 일치해야 함
 */
data class AchievementResponse(
    val userId: String = "",
    val registerAt: String = "",
    val achievementId: Int = -1,
    val recordType: Int = RECORD_TYPE_ACHIEVEMENT,
) {
    companion object {
        const val RECORD_TYPE_ACHIEVEMENT = 2
    }
}