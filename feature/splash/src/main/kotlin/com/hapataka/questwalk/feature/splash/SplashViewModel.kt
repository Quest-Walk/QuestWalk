package com.hapataka.questwalk.feature.splash

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.di.ApplicationScope
import com.hapataka.questwalk.core.domain.usecase.CheckUserLoggedInUseCase
import com.hapataka.questwalk.core.domain.usecase.FetchUserInfoUseCase
import com.hapataka.questwalk.core.domain.usecase.GetUserInfoUseCase
import com.hapataka.questwalk.core.domain.usecase.ReconcileUserAggregateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
    private val fetchUserInfoUseCase: FetchUserInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val reconcileUserAggregateUseCase: ReconcileUserAggregateUseCase,
    @ApplicationScope
    private val externalScope: CoroutineScope,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<SplashEvent>()
    val event = _event.asSharedFlow()

    init {
        onIntent(SplashIntent.AppStarted)
    }

    fun onIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.AppStarted -> checkLoggedIn()
        }
    }

    private fun checkLoggedIn() {
        viewModelScope.launch {
            val start = SystemClock.uptimeMillis()
            val isLoggedIn = checkUserLoggedInUseCase()
            var hasUserInfo = false

            if (isLoggedIn) {
                // 이미 설정을 마친 정보가 캐시에 있으면 원격 왕복을 기다릴 이유가 없다
                val cachedUser = getUserInfoUseCase().first()
                hasUserInfo = cachedUser != null && cachedUser.userName.isNotBlank()

                if (hasUserInfo) {
                    // 화면을 막지 않는다. 동기화 결과가 로컬에 반영되면 구독 중인 화면이 알아서 받는다
                    externalScope.launch {
                        fetchUserInfoUseCase()
                        reconcileUserAggregateUseCase()
                    }
                } else {
                    // 판단할 근거가 없으니 이번만 원격을 기다린다
                    val fetchStart = SystemClock.uptimeMillis()
                    hasUserInfo = fetchUserInfoUseCase().isSuccess

                    if (hasUserInfo) {
                        externalScope.launch { reconcileUserAggregateUseCase() }
                    }
                }
            }

            // 최소 노출 시간이지 추가 대기가 아니다.
            // 그냥 delay를 걸면 준비에 걸린 시간만큼 스플래시가 통째로 길어진다
            val elapsed = SystemClock.uptimeMillis() - start
            if (elapsed < MIN_SPLASH_DURATION_MILLIS) {
                delay(MIN_SPLASH_DURATION_MILLIS - elapsed)
            }
            _event.emit(
                SplashEvent.NavigateToMain(
                    isLoggedIn = isLoggedIn,
                    hasUserInfo = hasUserInfo,
                )
            )
        }
    }
}

// 준비는 800ms 안팎이라 그대로 두면 로고가 스쳐 지나간다. 통념 상한선에 맞춘다
private const val MIN_SPLASH_DURATION_MILLIS = 1500L

sealed interface SplashUiState {
    data object Loading : SplashUiState
}

sealed interface SplashIntent {
    data object AppStarted : SplashIntent
}

sealed interface SplashEvent {
    data class NavigateToMain(
        val isLoggedIn: Boolean,
        val hasUserInfo: Boolean,
    ) : SplashEvent
}
