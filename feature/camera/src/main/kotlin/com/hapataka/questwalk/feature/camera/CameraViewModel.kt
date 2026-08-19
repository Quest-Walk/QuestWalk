package com.hapataka.questwalk.feature.camera

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.CompleteQuestWithPhotoUseCase
import com.hapataka.questwalk.core.domain.usecase.FailureReason
import com.hapataka.questwalk.core.domain.usecase.GetCurrentQuestUseCase
import com.hapataka.questwalk.core.domain.usecase.QuestCompletionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val getCurrentQuestUseCase: GetCurrentQuestUseCase,
    private val completeQuestWithPhotoUseCase: CompleteQuestWithPhotoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CameraEvent>()
    val event = _event.asSharedFlow()

    init {
        observeCurrentQuest()
    }

    private fun observeCurrentQuest() {
        viewModelScope.launch {
            getCurrentQuestUseCase().collect { quest ->
                _uiState.update { it.copy(keyword = quest?.keyword ?: "") }
            }
        }
    }

    fun toggleFlash() {
        _uiState.update { it.copy(isFlashOn = !it.isFlashOn) }
    }

    fun setCapturing(capturing: Boolean) {
        _uiState.update {
            it.copy(
                isCapturing = capturing,
                failureMessage = if (capturing) null else it.failureMessage,
                ocrDebugText = if (capturing) null else it.ocrDebugText,
            )
        }
    }

    fun setCapturedPhoto(bytes: ByteArray?) {
        _uiState.update { it.copy(capturedPhotoBytes = bytes) }
    }

    fun clearFailure() {
        _uiState.update { it.copy(failureMessage = null, ocrDebugText = null) }
    }

    fun processPhoto(filePath: String) {
        if (_uiState.value.isProcessing) return

        viewModelScope.launch {
            // 촬영 완료, 처리 시작 - 캡처 이미지는 유지
            _uiState.update {
                it.copy(
                    isCapturing = false,
                    isProcessing = true,
                    failureMessage = null,
                    ocrDebugText = null,
                )
            }

            when (val result = completeQuestWithPhotoUseCase(filePath)) {
                is QuestCompletionResult.Success -> {
                    Log.d(TAG, "Quest OCR success: ${result.matchResult.toLogText()}")
                    // 성공 시 상태 리셋 후 홈으로 복귀
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            capturedPhotoBytes = null,
                            failureMessage = null,
                            ocrDebugText = null,
                        )
                    }
                    _event.emit(CameraEvent.QuestSuccess)
                }
                is QuestCompletionResult.Failure -> {
                    // 실패 시 캡처 이미지 숨기고 카메라 프리뷰로 복귀
                    val message = when (result.reason) {
                        FailureReason.NO_ACTIVE_QUEST -> "진행 중인 퀘스트가 없습니다."
                        FailureReason.OCR_ERROR -> "텍스트 인식에 실패했습니다. 다시 시도해주세요."
                        FailureReason.KEYWORD_NOT_MATCHED -> "키워드가 보이게 사진을 다시 찍어주세요"
                    }
                    val debugText = result.matchResult?.toLogText()
                    Log.w(TAG, "Quest OCR failed: reason=${result.reason}, $debugText")

                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            capturedPhotoBytes = null,
                            failureMessage = message,
                            ocrDebugText = debugText,
                        )
                    }
                }
            }
        }
    }

    private fun com.hapataka.questwalk.core.domain.repository.KeywordMatchResult.toLogText(): String {
        return "similarity=${"%.2f".format(similarity)}, matched='$matchedText', recognized='$recognizedText'"
    }

    private companion object {
        private const val TAG = "CameraViewModel"
    }
}

data class CameraUiState(
    val keyword: String = "",
    val isFlashOn: Boolean = false,
    val isCapturing: Boolean = false,
    val isProcessing: Boolean = false,
    val capturedPhotoBytes: ByteArray? = null,
    val failureMessage: String? = null,
    val ocrDebugText: String? = null,
)

sealed class CameraEvent {
    data object QuestSuccess : CameraEvent()

    data class QuestFailed(
        val message: String,
    ) : CameraEvent()
}
