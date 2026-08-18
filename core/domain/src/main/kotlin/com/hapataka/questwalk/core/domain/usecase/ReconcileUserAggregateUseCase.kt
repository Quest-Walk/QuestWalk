package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import com.hapataka.questwalk.core.model.History
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

/**
 * 유저 누적 정보를 히스토리 기준으로 다시 계산해 맞춘다.
 *
 * 누적값을 더하지 않고 매번 전체에서 절대값으로 다시 구하기 때문에
 * 몇 번을 실행해도 결과가 같고, 중간에 반영되지 못한 기록도 함께 복구된다.
 *
 * @return 실제로 갱신했으면 true, 이미 최신이라 건너뛰었으면 false
 */
class ReconcileUserAggregateUseCase @Inject constructor(
    @Named("DefaultAuthRepository")
    private val authRepository: AuthRepository,
    private val historyRepository: HistoryRepository,
    @Named("DefaultUserRepository")
    private val userRepository: UserRepositoryNew,
) {
    suspend operator fun invoke(): Result<Boolean> = kotlin.runCatching {
        val userId = authRepository.getUserId()
        val lastAggregatedAt = userRepository.getLastAggregatedAt(userId).getOrThrow()
        val histories = historyRepository.getUserHistories(userId).getOrThrow()

        val questResults = histories
            .filterIsInstance<History.QuestResult>()
            .filter { it.isSuccess }
        val achievements = histories.filterIsInstance<History.Achievement>()

        val latestStamp = questResults
            .map { it.registerAtUtc }
            .filter { it.isNotEmpty() }
            .maxOrNull()
            .orEmpty()

        // 집계한 적이 있고 그 뒤로 새 기록이 없으면 건너뛴다
        if (lastAggregatedAt.isNotEmpty() && latestStamp <= lastAggregatedAt) {
            return@runCatching false
        }

        userRepository.applyAggregate(
            userId = userId,
            totalTime = questResults.sumOf { it.duration },
            totalDistance = questResults.map { it.distance }.sum(),
            totalStep = questResults.sumOf { it.step },
            successKeywords = questResults.map { it.questKeyword }.distinct(),
            achievementIds = achievements.map { it.achievementId }.distinct(),
            // UTC 시각을 못 구하는 과거 기록만 있는 경우엔 현재 시각을 기준선으로 삼는다
            lastAggregatedAt = latestStamp.ifEmpty { nowUtcStamp() },
        ).getOrThrow()

        true
    }

    private fun nowUtcStamp(): String =
        Instant.now().atZone(ZoneOffset.UTC).format(UTC_STAMP_FORMATTER)

    companion object {
        // 고정 폭이라 사전순 비교가 곧 시간순 비교가 된다
        private val UTC_STAMP_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    }
}
