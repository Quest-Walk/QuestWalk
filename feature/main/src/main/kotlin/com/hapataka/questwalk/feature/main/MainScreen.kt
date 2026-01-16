package com.hapataka.questwalk.feature.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hapataka.questwalk.feature.main.navigation.MainNavHost
import com.hapataka.questwalk.feature.main.navigation.MainNavigator
import com.hapataka.questwalk.feature.main.navigation.rememberMainNavigator
import com.hapataka.questwalk.core.designsystem.theme.QuestWalkTheme
import com.hapataka.questwalk.core.navigation.OnboardingStep
import com.hapataka.questwalk.core.ui.LocalPaddingValues

@Composable
fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
) {
    var lightBarEnable by rememberSaveable { mutableStateOf(false) }

    navigator.navController.addOnDestinationChangedListener { _, destination, _ ->
        val route = destination.route?.substringAfterLast(".")
        lightBarEnable = route == OnboardingStep.Join::class.simpleName
    }

    QuestWalkTheme(lightBar = lightBarEnable) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.systemBars,
        ) { paddingValues ->
            CompositionLocalProvider(
                LocalPaddingValues provides paddingValues
            ) {
                MainNavHost(
                    navigator = navigator,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
