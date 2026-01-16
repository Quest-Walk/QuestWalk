package com.hapataka.questwalk.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Onboarding : Route
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

/**
 * 메인 앱 화면 Route
 */
@Serializable
sealed interface MainRoute {
    @Serializable
    data object Home : MainRoute

    @Serializable
    data object Quest : MainRoute

    @Serializable
    data object Record : MainRoute

    @Serializable
    data object Weather : MainRoute

    @Serializable
    data object MyInfo : MainRoute

    @Serializable
    data object Camera : MainRoute
}

/**
 * Home 화면에서 파생되는 상세 Route
 */
@Serializable
sealed interface HomeRoute {
    @Serializable
    data class QuestDetail(val keyword: String) : HomeRoute

    @Serializable
    data class Result(val resultId: String) : HomeRoute
}