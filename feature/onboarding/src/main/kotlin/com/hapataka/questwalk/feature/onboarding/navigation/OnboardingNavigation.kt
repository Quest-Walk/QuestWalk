package com.hapataka.questwalk.feature.onboarding.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.Route
import com.hapataka.questwalk.feature.onboarding.OnboardingScreen

fun NavController.navigateOnboarding(navOptions: NavOptions) {
    navigate(Route.Onboarding, navOptions)
}

fun NavGraphBuilder.onboardingNavGraph(
    navigateToHome: () -> Unit,
) {
    composable<Route.Onboarding> {
        OnboardingScreen(
            isLoggedIn = false,
            navigateToHome = navigateToHome,
        )
    }
}