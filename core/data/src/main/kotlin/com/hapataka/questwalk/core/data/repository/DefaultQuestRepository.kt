package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.domain.di.ApplicationScope
import com.hapataka.questwalk.core.dataapi.datasource.ImageStorageDataSource
import com.hapataka.questwalk.core.dataapi.datasource.QuestRemoteDataSource
import com.hapataka.questwalk.core.dataapi.datasource.TextRecognitionDataSource
import com.hapataka.questwalk.core.dataapi.model.QuestDto
import com.hapataka.questwalk.core.dataapi.model.SuccessItemDto
import com.hapataka.questwalk.core.domain.repository.ImageUploadResult
import com.hapataka.questwalk.core.domain.repository.KeywordMatchResult
import com.hapataka.questwalk.core.domain.repository.QuestRepository
import com.hapataka.questwalk.core.model.Quest
import info.debatty.java.stringsimilarity.NormalizedLevenshtein
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultQuestRepository @Inject constructor(
    private val questRemoteDataSource: QuestRemoteDataSource,
    private val textRecognitionDataSource: TextRecognitionDataSource,
    private val imageStorageDataSource: ImageStorageDataSource,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : QuestRepository {

    private val _currentQuest = MutableStateFlow<Quest?>(null)
    override val currentQuest: StateFlow<Quest?> = _currentQuest.asStateFlow()

    private val levenshtein = NormalizedLevenshtein()

    private var uploadDeferred: Deferred<Result<ImageUploadResult>>? = null

    override fun setCurrentQuest(quest: Quest) {
        _currentQuest.value = quest
    }

    override fun clearCurrentQuest() {
        _currentQuest.value = null
    }

    override suspend fun getAllQuests(): Result<List<Quest>> {
        return questRemoteDataSource.getAllQuests().map { dtos ->
            dtos.map { it.toModel() }
        }
    }

    override suspend fun getQuestByKeyword(keyword: String): Result<Quest> {
        return questRemoteDataSource.getQuestByKeyword(keyword).map { dto ->
            dto?.toModel() ?: throw NoSuchElementException("Quest not found: $keyword")
        }
    }

    override suspend fun updateQuestSuccess(
        keyword: String,
        userId: String,
        imageUrl: String,
        registerAt: String,
    ): Result<Unit> {
        val successItem = SuccessItemDto(
            userId = userId,
            imageUrl = imageUrl,
            registerAt = registerAt,
        )
        return questRemoteDataSource.updateQuestSuccess(keyword, successItem)
    }

    override suspend fun verifyPhotoKeyword(filePath: String, keyword: String): Result<KeywordMatchResult> {
        return textRecognitionDataSource.recognizeText(filePath).map { recognizedText ->
            val normalizedRecognizedText = normalizeForOcrMatch(recognizedText)
            val normalizedKeyword = normalizeForOcrMatch(keyword)

            val (matchedText, similarity) = findBestMatch(normalizedRecognizedText, normalizedKeyword)

            KeywordMatchResult(
                isSuccess = similarity >= SIMILARITY_THRESHOLD,
                recognizedText = recognizedText,
                matchedText = matchedText,
                similarity = similarity,
            )
        }
    }

    private fun normalizeForOcrMatch(value: String): String {
        return Normalizer.normalize(value, Normalizer.Form.NFKC)
            .lowercase(Locale.KOREAN)
            .filter { it.isLetterOrDigit() }
    }

    override suspend fun uploadQuestImage(
        filePath: String,
        userId: String,
        keyword: String,
    ): Result<ImageUploadResult> {
        val timestamp = System.currentTimeMillis()
        val uniqueId = UUID.randomUUID().toString().take(8)
        val fileName = "${keyword}_${timestamp}_$uniqueId.jpg"
        val remotePath = "quest_images/$userId/$fileName"

        val remoteUrlResult = imageStorageDataSource.uploadImage(filePath, remotePath)
        if (remoteUrlResult.isFailure) {
            return Result.failure(remoteUrlResult.exceptionOrNull() ?: Exception("Upload failed"))
        }

        val localPathResult = imageStorageDataSource.copyToLocalStorage(filePath, fileName)
        if (localPathResult.isFailure) {
            return Result.failure(localPathResult.exceptionOrNull() ?: Exception("Local copy failed"))
        }

        return Result.success(
            ImageUploadResult(
                remoteUrl = remoteUrlResult.getOrThrow(),
                localPath = localPathResult.getOrThrow(),
            )
        )
    }

    override fun startBackgroundUpload(filePath: String, userId: String, keyword: String) {
        val deferred = CompletableDeferred<Result<ImageUploadResult>>()
        uploadDeferred = deferred
        applicationScope.launch {
            val result = uploadQuestImage(filePath, userId, keyword)
            deferred.complete(result)
        }
    }

    override suspend fun awaitUploadResult(): Result<ImageUploadResult> {
        return uploadDeferred?.await()
            ?: Result.failure(IllegalStateException("No background upload in progress"))
    }

    override suspend fun deletePhotoFile(filePath: String) {
        imageStorageDataSource.deleteFile(filePath)
    }

    private fun findBestMatch(text: String, keyword: String): Pair<String, Double> {
        if (text.isEmpty() || keyword.isEmpty()) {
            return "" to 0.0
        }

        if (text.contains(keyword)) {
            return keyword to 1.0
        }

        var bestMatch = ""
        var bestSimilarity = 0.0

        val keywordLength = keyword.length
        val minWindowSize = (keywordLength - 2).coerceAtLeast(1)
        val maxWindowSize = (keywordLength + 3).coerceAtMost(text.length)
        for (windowSize in minWindowSize..maxWindowSize) {
            for (i in 0..(text.length - windowSize)) {
                val substring = text.substring(i, i + windowSize)
                val similarity = 1.0 - levenshtein.distance(substring, keyword)
                if (similarity > bestSimilarity) {
                    bestSimilarity = similarity
                    bestMatch = substring
                }
            }
        }

        return bestMatch to bestSimilarity
    }

    private fun QuestDto.toModel(): Quest = Quest(
        keyword = keyword,
        level = level,
        successItems = successItems.map { it.toModel() },
    )

    private fun SuccessItemDto.toModel(): Quest.SuccessItem = Quest.SuccessItem(
        userId = userId,
        imageUrl = imageUrl,
        registerAt = registerAt,
    )

    companion object {
        private const val SIMILARITY_THRESHOLD = 0.7
    }
}
