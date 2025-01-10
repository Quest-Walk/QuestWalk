package com.hapataka.questwalk.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route
}

sealed interface OnboardingRoute : Route {
    @Serializable
    data object Login : Route
}