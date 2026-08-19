package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.dataapi.datasource.HistoryRemoteDataSource
import com.hapataka.questwalk.core.dataapi.model.HistoryDto
import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.model.Location
import com.hapataka.questwalk.core.model.UserActivitySummary
import com.hapataka.questwalk.core.remote.util.decryptECB
import com.hapataka.questwalk.core.remote.util.encryptECB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DefaultHistoryRepository @Inject constructor(
    private val historyRemoteDataSource: HistoryRemoteDataSource,
) : HistoryRepository {

    private val encryptionKey = "Q2CR35WC121QCB4T"

    override suspend fun getUserActivitySummary(userId: String): Result<UserActivitySummary> {
        // 경로 복호화를 건너뛴다. 집계에 쓰지 않는 값이라 통째로 푸는 비용이 크다
        return historyRemoteDataSource.getUserHistories(userId).map { dtos ->
            UserActivitySummary(
                successQuests = dtos
                    .filterIsInstance<HistoryDto.QuestResultDto>()
                    .filter { it.isSuccess }
                    .map { dto ->
                        UserActivitySummary.SuccessQuest(
                            questKeyword = dto.questKeyword,
                            duration = dto.duration,
                            distance = dto.distance,
                            step = dto.step,
                            registerAtUtc = dto.registerAtUtc,
                        )
                    },
                achievementIds = dtos
                    .filterIsInstance<HistoryDto.AchievementDto>()
                    .map { it.achievementId },
            )
        }
    }

    // 경로 복호화와 파싱은 무겁다. 호출부 디스패처(주로 메인)에서 돌지 않도록 옮긴다
    override suspend fun getUserHistories(userId: String): Result<List<History>> {
        val dtos = historyRemoteDataSource.getUserHistories(userId)
        return withContext(Dispatchers.Default) {
            dtos.map { list -> list.map { it.toModel() } }
        }
    }

    override suspend fun getQuestResult(resultId: String): Result<History.QuestResult> {
        val dto = historyRemoteDataSource.getQuestResult(resultId)
        return withContext(Dispatchers.Default) {
            dto.map { it.toModel() }
        }
    }

    override suspend fun postHistory(userId: String, history: History): Result<String> {
        val dto = history.toDto()
        return historyRemoteDataSource.postHistory(dto)
    }

    override suspend fun deleteHistoriesById(userId: String): Result<Unit> {
        return historyRemoteDataSource.deleteHistoriesByUserId(userId)
    }

    // DTO -> Model 변환
    private fun HistoryDto.toModel(): History = when (this) {
        is HistoryDto.QuestResultDto -> toModel()
        is HistoryDto.AchievementDto -> toModel()
    }

    private fun HistoryDto.QuestResultDto.toModel(): History.QuestResult =
        History.QuestResult(
            id = resultId,
            userId = userId,
            registerAt = LocalDateTime.parse(registerAt),
            questKeyword = questKeyword,
            duration = duration,
            distance = distance,
            step = step,
            isSuccess = isSuccess,
            route = route.decryptToLocationList(),
            successLocation = successLocation?.decryptToLocation(),
            imageUrl = imageUrl,
            registerAtUtc = registerAtUtc,
        )

    private fun HistoryDto.AchievementDto.toModel(): History.Achievement =
        History.Achievement(
            id = resultId,
            userId = userId,
            registerAt = LocalDateTime.parse(registerAt),
            achievementId = achievementId,
        )

    // Model -> DTO 변환
    private fun History.toDto(): HistoryDto = when (this) {
        is History.QuestResult -> toDto()
        is History.Achievement -> toDto()
    }

    private fun History.QuestResult.toDto(): HistoryDto.QuestResultDto =
        HistoryDto.QuestResultDto(
            resultId = id,
            userId = userId,
            registerAt = registerAt.toString(),
            questKeyword = questKeyword,
            duration = duration,
            distance = distance,
            step = step,
            isSuccess = isSuccess,
            route = route.encryptECB(encryptionKey),
            successLocation = successLocation?.encryptECB(encryptionKey),
            imageUrl = imageUrl,
            registerAtUtc = registerAt.toUtcStamp(),
        )

    private fun History.Achievement.toDto(): HistoryDto.AchievementDto =
        HistoryDto.AchievementDto(
            resultId = id,
            userId = userId,
            registerAt = registerAt.toString(),
            achievementId = achievementId,
        )

    private fun LocalDateTime.toUtcStamp(): String =
        atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneOffset.UTC)
            .format(UTC_STAMP_FORMATTER)

    // 암호화된 문자열 -> Location 변환
    private fun String.decryptToLocationList(): List<Location> {
        if (this.isBlank()) return emptyList()
        return try {
            val decrypted = this.decryptECB(encryptionKey)
            parseLocationList(decrypted)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun String.decryptToLocation(): Location? {
        if (this.isBlank()) return null
        return try {
            val decrypted = this.decryptECB(encryptionKey)
            parseLocation(decrypted)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseLocationList(json: String): List<Location> {
        // JSON 형태: [{"first":lat,"second":lng},...] 또는 [[lat,lng],...]
        val pairRegex = """"first"\s*:\s*([^,}]+).*?"second"\s*:\s*([^,}]+)""".toRegex()
        val pairLocations = pairRegex.findAll(json).mapNotNull { match ->
            val (lat, lng) = match.destructured
            lat.toLocationOrNull(lng)
        }.toList()

        if (pairLocations.isNotEmpty()) {
            return pairLocations
        }

        val arrayRegex = """\[([^,\[\]]+),([^,\[\]]+)\]""".toRegex()
        return arrayRegex.findAll(json).mapNotNull { match ->
            val (lat, lng) = match.destructured
            lat.toLocationOrNull(lng)
        }.toList()
    }

    private fun String.toLocationOrNull(longitude: String): Location? {
        return runCatching {
            Location(this.toFloat(), longitude.toFloat())
        }.getOrNull()
    }

    companion object {
        // 고정 폭이라 사전순 비교가 곧 시간순 비교가 된다
        private val UTC_STAMP_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    }

    private fun parseLocation(json: String): Location? {
        // JSON 형태: {"first":lat,"second":lng} 또는 [lat,lng]
        val pairRegex = """"first"\s*:\s*([^,}]+).*"second"\s*:\s*([^,}]+)""".toRegex()
        val arrayRegex = """\[([^,\[\]]+),([^,\[\]]+)\]""".toRegex()

        pairRegex.find(json)?.let { match ->
            val (lat, lng) = match.destructured
            return Location(lat.toFloat(), lng.toFloat())
        }

        arrayRegex.find(json)?.let { match ->
            val (lat, lng) = match.destructured
            return Location(lat.toFloat(), lng.toFloat())
        }

        return null
    }
}
