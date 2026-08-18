package com.hapataka.questwalk.feature.myinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.DeleteAccountUseCase
import com.hapataka.questwalk.core.domain.usecase.FetchUserInfoUseCase
import com.hapataka.questwalk.core.domain.usecase.GetUserInfoUseCase
import com.hapataka.questwalk.core.domain.usecase.LogoutUseCase
import com.hapataka.questwalk.core.domain.usecase.ReconcileUserAggregateUseCase
import com.hapataka.questwalk.core.model.User
import com.hapataka.questwalk.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    private val fetchUserInfoUseCase: FetchUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val reconcileUserAggregateUseCase: ReconcileUserAggregateUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<MyInfoUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<MyInfoUiState>> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<MyInfoEvent>()
    val event = _event.asSharedFlow()

    private var loadJob: Job? = null
    private var syncJob: Job? = null

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

            MyInfoIntent.WithdrawConfirmed -> {
                if (state is UiState.Success) {
                    UiState.Success(state.data.copy(isDeletingAccount = true))
                } else {
                    state
                }
            }

            MyInfoIntent.Refresh -> state
        }
    }

    private fun handleSideEffect(intent: MyInfoIntent) {
        when (intent) {
            MyInfoIntent.Refresh -> refreshUserInfo()
            MyInfoIntent.LogoutClicked -> logout()
            MyInfoIntent.WithdrawClicked -> Unit
            MyInfoIntent.WithdrawConfirmed -> deleteAccount()
        }
    }

    /**
     * 로컬 캐시만 구독한다. 원격 동기화는 앱 시작과 퀘스트 완료 시점에 이미 돌고,
     * 그 결과가 로컬에 반영되면 이 구독이 새 값을 받는다.
     */
    private fun loadUserInfo() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            getUserInfoUseCase()
                .catch { e -> _uiState.value = UiState.Failure(e) }
                .collectLatest { user ->
                    if (user != null) {
                        _uiState.value = UiState.Success(user.toUiState())
                    } else {
                        _uiState.value = UiState.Failure(Exception("User not found"))
                    }
                }
        }
    }

    /** 사용자가 직접 요청했을 때만 원격까지 다녀온다. */
    private fun refreshUserInfo() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            reconcileUserAggregateUseCase()

            fetchUserInfoUseCase()
                .onFailure { error ->
                    _event.emit(MyInfoEvent.ShowMessage(error.message ?: "정보 동기화에 실패했습니다"))
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

    private fun deleteAccount() {
        viewModelScope.launch {
            deleteAccountUseCase()
                .onSuccess {
                    _event.emit(MyInfoEvent.ShowMessage("탈퇴가 완료되었습니다"))
                    _event.emit(MyInfoEvent.LogoutSuccess)
                }
                .onFailure { error ->
                    _event.emit(MyInfoEvent.ShowMessage(error.message ?: "탈퇴에 실패했습니다"))
                    _uiState.update { state ->
                        if (state is UiState.Success) {
                            UiState.Success(state.data.copy(isDeletingAccount = false))
                        } else {
                            state
                        }
                    }
                }
        }
    }

    private fun User.toUiState(): MyInfoUiState {
        val hours = (totalTime / 3600).toInt()
        val minutes = ((totalTime % 3600) / 60).toInt()
        val seconds = (totalTime % 60).toInt()
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
    val isDeletingAccount: Boolean = false,
)

sealed interface MyInfoIntent {
    data object Refresh : MyInfoIntent
    data object LogoutClicked : MyInfoIntent
    data object WithdrawClicked : MyInfoIntent
    data object WithdrawConfirmed : MyInfoIntent
}

sealed interface MyInfoEvent {
    data object LogoutSuccess : MyInfoEvent
    data class ShowMessage(val message: String) : MyInfoEvent
}
