package com.hapataka.questwalk.feature.onboarding.model

sealed interface JoinState {
    data object Idle : JoinState
    data object Loading : JoinState
    data object Success : JoinState
    data class Failure(val message: String) : JoinState
}
