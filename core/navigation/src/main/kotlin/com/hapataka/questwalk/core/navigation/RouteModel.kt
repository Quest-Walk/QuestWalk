package com.hapataka.questwalk.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data class Onboarding(val onboardingStep: OnboardingStep) : Route
}

@Serializable
sealed interface OnboardingStep {
    @Serializable
    data object Login : OnboardingStep

    @Serializable
    data object Join : OnboardingStep

    @Serializable
    data object Setup : OnboardingStep
}