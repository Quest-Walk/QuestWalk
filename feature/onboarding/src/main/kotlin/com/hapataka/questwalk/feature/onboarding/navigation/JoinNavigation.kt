package com.hapataka.questwalk.feature.onboarding.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.OnboardingRoute
import com.hapataka.questwalk.feature.onboarding.JoinRoute

fun NavController.navigateJoin(navOptions: NavOptions) {
    navigate(OnboardingRoute.Join, navOptions)
}

fun NavGraphBuilder.joinNavGraph(
    popBackStack: () -> Unit,
    padding: PaddingValues,
) {
    composable<OnboardingRoute.Join> {
        JoinRoute(
            padding = padding
        )
    }
}