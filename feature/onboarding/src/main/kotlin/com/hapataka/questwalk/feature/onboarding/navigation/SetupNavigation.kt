package com.hapataka.questwalk.feature.onboarding.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.OnboardingRoute
import com.hapataka.questwalk.feature.onboarding.SetupRoute

fun NavController.navigateSetup(navOptions: NavOptions) {
    navigate(OnboardingRoute.Setup, navOptions)
}

fun NavGraphBuilder.setupNavGraph(
    navigateToHome: () -> Unit,
    padding: PaddingValues,
) {
    composable<OnboardingRoute.Setup> {
        SetupRoute(
            navigateToHome = navigateToHome,
            padding = padding
        )
    }
}