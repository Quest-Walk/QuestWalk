package com.hapataka.questwalk.feature.onboarding.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.OnboardingRoute
import com.hapataka.questwalk.feature.onboarding.LoginRoute

fun NavController.navigateLogin(navOptions: NavOptions) {
    navigate(OnboardingRoute.Login, navOptions)
}

fun NavGraphBuilder.loginNavGraph(
    navigateToHome: () -> Unit,
    padding: PaddingValues,
) {
    composable<OnboardingRoute.Login> {
        LoginRoute(
            navigateToHome = navigateToHome,
            padding = padding
        )
    }
}