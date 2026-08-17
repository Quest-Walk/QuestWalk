package com.hapataka.questwalk.feature.myinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetUserInfoUseCase
import com.hapataka.questwalk.core.domain.usecase.LogoutUseCase
import com.hapataka.questwalk.core.model.User
import com.hapataka.questwalk.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyInfoViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<MyInfoUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<MyInfoUiState>> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<MyInfoEvent>()
    val event = _event.asSharedFlow()

    init {
        loadUserInfo()
    }

    fun onIntent(intent: MyInfoIntent) {
        _uiState.update { state -> reduce(state, intent) }
        handleSideEffect(intent)
    }

    private fun reduce(
        state: UiState<MyInfoUiState>,
        intent: MyInfoIntent,
    ): UiState<MyInfoUiState> {
        return when (intent) {
            MyInfoIntent.LogoutClicked,
            MyInfoIntent.WithdrawClicked,
            -> state

            MyInfoIntent.Refresh -> UiState.Loading
        }
    }

    private fun handleSideEffect(intent: MyInfoIntent) {
        when (intent) {
            MyInfoIntent.Refresh -> loadUserInfo()
            MyInfoIntent.LogoutClicked -> logout()
            MyInfoIntent.WithdrawClicked -> {
                viewModelScope.launch {
                    _event.emit(MyInfoEvent.ShowMessage("탈퇴 기능은 아직 준비 중입니다"))
                }
            }
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            getUserInfoUseCase()
                .catch { e ->
                    _uiState.value = UiState.Failure(e)
                }
                .collectLatest { user ->
                    if (user != null) {
                        _uiState.value = UiState.Success(user.toUiState())
                    } else {
                        _uiState.value = UiState.Failure(Exception("User not found"))
                    }
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                logoutUseCase()
                _event.emit(MyInfoEvent.LogoutSuccess)
            } catch (e: Exception) {
                _uiState.value = UiState.Failure(e)
            }
        }
    }

    private fun User.toUiState(): MyInfoUiState {
        val hours = (totalTime / 1000 / 3600).toInt()
        val minutes = ((totalTime / 1000 % 3600) / 60).toInt()
        val seconds = (totalTime / 1000 % 60).toInt()
        val kcal = totalStep * 0.04 // 걸음당 약 0.04kcal

        return MyInfoUiState(
            userName = userName,
            characterType = characterType.id,
            totalTimeFormatted = "${hours}시간 ${minutes}분 ${seconds}초",
            totalDistanceFormatted = if (totalDistance >= 1000) {
                String.format("%.1fkm", totalDistance / 1000)
            } else {
                "${totalDistance.toInt()}m"
            },
            totalStepFormatted = "${totalStep}걸음",
            totalKcalFormatted = String.format("%.1fKcal", kcal),
            questCount = successKeywords.size,
            achievementCount = achievementIds.size,
        )
    }
}

data class MyInfoUiState(
    val userName: String = "",
    val characterType: Int = 1,
    val totalTimeFormatted: String = "0시간 0분 0초",
    val totalDistanceFormatted: String = "0m",
    val totalStepFormatted: String = "0걸음",
    val totalKcalFormatted: String = "0.0Kcal",
    val questCount: Int = 0,
    val achievementCount: Int = 0,
)

sealed interface MyInfoIntent {
    data object Refresh : MyInfoIntent
    data object LogoutClicked : MyInfoIntent
    data object WithdrawClicked : MyInfoIntent
}

sealed interface MyInfoEvent {
    data object LogoutSuccess : MyInfoEvent
    data class ShowMessage(val message: String) : MyInfoEvent
}
