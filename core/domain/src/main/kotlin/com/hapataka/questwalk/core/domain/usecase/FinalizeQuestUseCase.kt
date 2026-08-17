package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.core.domain.repository.PlaySessionRepository
import com.hapataka.questwalk.core.domain.repository.QuestRepository
import com.hapataka.questwalk.core.model.History
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

class FinalizeQuestUseCase @Inject constructor(
    private val questRepository: QuestRepository,
    private val playSessionRepository: PlaySessionRepository,
    private val historyRepository: HistoryRepository,
    @Named("DefaultAuthRepository")
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): String {
        val session = playSessionRepository.sessionState.value
        val userId = authRepository.getUserId()
        val keyword = session.keyword

        val uploadResult = questRepository.awaitUploadResult().getOrThrow()

        val now = LocalDateTime.now()
        val history = History.QuestResult(
            id = "",
            userId = userId,
            registerAt = now,
            questKeyword = keyword,
            duration = session.duration,
            distance = session.distance,
            step = session.steps,
            isSuccess = true,
            route = session.route,
            successLocation = session.successLocation,
            imageUrl = uploadResult.remoteUrl,
        )

        val resultId = historyRepository.postHistory(userId, history).getOrThrow()

        val registerAt = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        questRepository.updateQuestSuccess(keyword, userId, uploadResult.remoteUrl, registerAt)

        questRepository.deletePhotoFile(uploadResult.localPath)

        return resultId
    }
}
