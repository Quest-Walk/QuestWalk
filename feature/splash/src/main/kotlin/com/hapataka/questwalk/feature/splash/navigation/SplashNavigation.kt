package com.hapataka.questwalk.feature.splash.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.Route
import com.hapataka.questwalk.feature.splash.SplashScreen
import com.hapataka.questwalk.feature.splash.SplashUiState
import com.hapataka.questwalk.feature.splash.SplashViewModel

fun NavGraphBuilder.splashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
) {
    composable<Route.Splash> {
        val viewModel: SplashViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState) {
            when (val state = uiState) {
                is SplashUiState.NavigateToMain -> {
                    if (state.isLoggedIn) {
                        onNavigateToHome()
                    } else {
                        onNavigateToOnboarding()
                    }
                }
                else -> {}
            }
        }

        SplashScreen()
    }
}
