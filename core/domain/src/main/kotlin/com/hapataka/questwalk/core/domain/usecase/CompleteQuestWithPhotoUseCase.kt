package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import com.hapataka.questwalk.core.domain.repository.KeywordMatchResult
import com.hapataka.questwalk.core.domain.repository.LocationRepository
import com.hapataka.questwalk.core.domain.repository.PlaySessionRepository
import com.hapataka.questwalk.core.domain.repository.QuestRepository
import javax.inject.Inject
import javax.inject.Named

class CompleteQuestWithPhotoUseCase @Inject constructor(
    private val questRepository: QuestRepository,
    private val playSessionRepository: PlaySessionRepository,
    private val locationRepository: LocationRepository,
    @Named("DefaultAuthRepository")
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(filePath: String): QuestCompletionResult {
        val session = playSessionRepository.sessionState.value
        val keyword = session.keyword

        if (keyword.isBlank()) {
            questRepository.deletePhotoFile(filePath)
            return QuestCompletionResult.Failure(
                reason = FailureReason.NO_ACTIVE_QUEST,
                message = "진행 중인 퀘스트가 없습니다.",
            )
        }

        val verifyResult = questRepository.verifyPhotoKeyword(filePath, keyword)
        if (verifyResult.isFailure) {
            questRepository.deletePhotoFile(filePath)
            return QuestCompletionResult.Failure(
                reason = FailureReason.OCR_ERROR,
                message = "텍스트 인식에 실패했습니다.",
            )
        }

        val matchResult = verifyResult.getOrThrow()
        if (!matchResult.isSuccess) {
            questRepository.deletePhotoFile(filePath)
            return QuestCompletionResult.Failure(
                reason = FailureReason.KEYWORD_NOT_MATCHED,
                message = "키워드가 일치하지 않습니다.",
                matchResult = matchResult,
            )
        }

        val userId = authRepository.getUserId()
        questRepository.startBackgroundUpload(filePath, userId, keyword)

        val currentLocation = session.route.lastOrNull() ?: locationRepository.getCurrentLocation()
        playSessionRepository.markSuccess(currentLocation)

        return QuestCompletionResult.Success(
            keyword = keyword,
            matchResult = matchResult,
        )
    }
}

sealed class QuestCompletionResult {
    data class Success(
        val keyword: String,
        val matchResult: KeywordMatchResult,
    ) : QuestCompletionResult()

    data class Failure(
        val reason: FailureReason,
        val message: String,
        val matchResult: KeywordMatchResult? = null,
    ) : QuestCompletionResult()
}

enum class FailureReason {
    NO_ACTIVE_QUEST,
    OCR_ERROR,
    KEYWORD_NOT_MATCHED,
}
