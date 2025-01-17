package com.hapataka.questwak.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.hapataka.questwak.feature.navigation.MainNavigator
import com.hapataka.questwak.feature.navigation.rememberMainNavigator
import com.hapataka.questwalk.core.designsystem.theme.QuestWalkTheme
import com.hapataka.questwalk.core.navigation.OnboardingStep
import com.hapataka.questwalk.core.navigation.Route
import com.hapataka.questwalk.feature.onboarding.navigation.onboardingNavGraph

@Composable
fun MainScreen(
    navigateToHome: () -> Unit,
    startDestination: Route = Route.Onboarding(OnboardingStep.Login),
    navigator: MainNavigator = rememberMainNavigator(),
) {
    var lightBarEnable by rememberSaveable { mutableStateOf(false) }

    navigator.navController.addOnDestinationChangedListener { _, destination, _ ->
        lightBarEnable =
            destination.route?.substringAfterLast(".") == OnboardingStep.Join.toString()
    }

    QuestWalkTheme(lightBarEnable) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
        ) { padding ->
            MainContent(
                startDestination = startDestination,
                navigateToHome = navigateToHome,
                navigator = navigator,
                padding = padding,
            )
        }
    }
}

@Composable
private fun MainContent(
    startDestination: Route = Route.Onboarding(OnboardingStep.Login),
    navigateToHome: () -> Unit = {},
    navigator: MainNavigator = rememberMainNavigator(),
    padding: PaddingValues = PaddingValues(),
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NavHost(
            navController = navigator.navController,
            startDestination = startDestination,
        ) {
            onboardingNavGraph(
                navigateToHome = navigateToHome,
                padding = padding
            )
//
//            joinNavGraph(
//                popBackStack = navigator::popBackStack,
//                padding = padding
//            )
//
//            setupNavGraph(
//                navigateToHome = navigateToHome,
//                padding = padding
//            )
        }
    }
}
