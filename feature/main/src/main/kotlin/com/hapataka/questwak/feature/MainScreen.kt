package com.hapataka.questwak.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.hapataka.questwak.feature.navigation.MainNavigator
import com.hapataka.questwak.feature.navigation.rememberMainNavigator
import com.hapataka.questwalk.feature.onboarding.navigation.loginNavGraph

@Composable
fun MainScreen(
    navigateToHome: () -> Unit,
    navigator: MainNavigator = rememberMainNavigator(),
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        MainNavHost(
            navigateToHome = navigateToHome,
            navigator = navigator,
            padding = padding
        )
    }
}

@Composable
internal fun MainNavHost(
    navigateToHome: () -> Unit,
    navigator: MainNavigator,
    padding: PaddingValues = PaddingValues(),
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NavHost(
            navController = navigator.navController,
            startDestination = navigator.startDestination
        ) {
            loginNavGraph(
                navigateToHome = navigateToHome,
                padding = padding
            )
        }
    }
}

