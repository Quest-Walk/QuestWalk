package com.hapataka.questwak.feature.main.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.hapataka.questwalk.core.navigation.Route
import com.hapataka.questwalk.feature.onboarding.navigation.onboardingNavGraph

@Composable
internal fun MainNavHost(
    isLoggedIn: Boolean = false,
    startDestination: Route = Route.Onboarding,
    navigateToHome: () -> Unit = {},
    navigator: MainNavigator = rememberMainNavigator(),
    padding: PaddingValues = PaddingValues(),
) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navigator.navController,
        startDestination = startDestination,
    ) {
        onboardingNavGraph(
            isLoggedIn = isLoggedIn,
            navigateToHome = navigateToHome,
            padding = padding
        )
    }
}