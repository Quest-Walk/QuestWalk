package com.hapataka.questwalk.feature.onboarding.screen.join

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.JoinWithEmailUseCase
import com.hapataka.questwalk.feature.onboarding.model.JoinState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val joinWithEmailUseCase: JoinWithEmailUseCase,
) : ViewModel() {
    private val _joinState = MutableStateFlow<JoinState>(JoinState.Idle)
    val joinState = _joinState.asStateFlow()

    fun joinWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _joinState.update { JoinState.Loading }
            joinWithEmailUseCase(email, password)
                .onSuccess { _joinState.update { JoinState.Success } }
                .onFailure { e ->
                    Log.e(this.javaClass.simpleName, "fatal: ${e.message}")
                    _joinState.update {
                        JoinState.Failure(e.message ?: "알 수 없는 오류")
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("logoutTest", "JoinViewModel Cleared")
    }
}