package com.hapataka.questwalk.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.CheckUserLoggedInUseCase
import com.hapataka.questwalk.core.domain.usecase.FetchUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
            val isLoggedIn = checkUserLoggedInUseCase()
            var hasUserInfo = false

            if (isLoggedIn) {
                hasUserInfo = fetchUserInfoUseCase().isSuccess
            }

            delay(2000L)
            _event.emit(
                SplashEvent.NavigateToMain(
                    isLoggedIn = isLoggedIn,
                    hasUserInfo = hasUserInfo,
                )
            )
        }
    }
}

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
