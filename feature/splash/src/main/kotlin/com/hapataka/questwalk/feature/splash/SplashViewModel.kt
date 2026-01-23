package com.hapataka.questwalk.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.CheckUserLoggedInUseCase
import com.hapataka.questwalk.core.domain.usecase.FetchUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
    private val fetchUserInfoUseCase: FetchUserInfoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        checkLoggedIn()
    }

    private fun checkLoggedIn() {
        viewModelScope.launch {
            val isLoggedIn = checkUserLoggedInUseCase()
            var hasUserInfo = false

            if (isLoggedIn) {
                hasUserInfo = fetchUserInfoUseCase().isSuccess
            }

            // 애니메이션이 보이도록 최소 2초 대기
            kotlinx.coroutines.delay(2000L)

            _uiState.update {
                SplashUiState.NavigateToMain(
                    isLoggedIn = isLoggedIn,
                    hasUserInfo = hasUserInfo,
                )
            }
        }
    }
}

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data class NavigateToMain(
        val isLoggedIn: Boolean,
        val hasUserInfo: Boolean,
    ) : SplashUiState
}
