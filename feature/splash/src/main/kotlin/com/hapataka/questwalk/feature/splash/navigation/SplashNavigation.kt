package com.hapataka.questwalk.feature.splash.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.Route
import com.hapataka.questwalk.feature.splash.SplashEvent
import com.hapataka.questwalk.feature.splash.SplashScreen
import com.hapataka.questwalk.feature.splash.SplashViewModel
import kotlinx.coroutines.flow.collectLatest

fun NavGraphBuilder.splashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: (isLoggedIn: Boolean) -> Unit,
) {
    composable<Route.Splash> {
        val viewModel: SplashViewModel = hiltViewModel()

        LaunchedEffect(viewModel) {
            viewModel.event.collectLatest { event ->
                when (event) {
                    is SplashEvent.NavigateToMain -> {
                        if (event.isLoggedIn && event.hasUserInfo) {
                            onNavigateToHome()
                        } else {
                            onNavigateToOnboarding(event.isLoggedIn)
                        }
                    }
                }
            }
        }

        SplashScreen()
    }
}
