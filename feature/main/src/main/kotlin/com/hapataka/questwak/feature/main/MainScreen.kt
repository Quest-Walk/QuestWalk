package com.hapataka.questwak.feature.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hapataka.questwak.feature.main.navigation.MainNavHost
import com.hapataka.questwak.feature.main.navigation.MainNavigator
import com.hapataka.questwak.feature.main.navigation.rememberMainNavigator
import com.hapataka.questwalk.core.designsystem.theme.QuestWalkTheme
import com.hapataka.questwalk.core.navigation.OnboardingStep

@Composable
fun MainScreen(
    navigateToHome: () -> Unit,
    isLoggedIn: Boolean = false,
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

            MainNavHost(
                isLoggedIn = isLoggedIn,
                navigateToHome = navigateToHome,
                navigator = navigator,
                padding = padding,
            )
        }
    }
}


