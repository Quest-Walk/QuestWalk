package com.hapataka.questwalk.feature.onboarding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.hapataka.questwalk.core.navigation.OnboardingStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _currentStep = MutableStateFlow(
        requireNotNull(
            savedStateHandle.get<String>(ONBOARDING_STEP)
                ?.let { Json.decodeFromString<OnboardingStep>(it) }
        ) { "존재하지 않는 Route입니다." }
    )
    val currentStep = _currentStep.asStateFlow()

    fun setCurrentStep(step: OnboardingStep) {
        _currentStep.update { step }
    }

    companion object {
        private const val ONBOARDING_STEP = "onboardingStep"
    }
}