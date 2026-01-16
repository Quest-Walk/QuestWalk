package com.hapataka.questwalk.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.hapataka.questwalk.feature.home.navigation.homeScreen
import com.hapataka.questwalk.feature.myinfo.navigation.myInfoScreen
import com.hapataka.questwalk.feature.onboarding.navigation.onboardingNavGraph
import com.hapataka.questwalk.feature.quest.navigation.questDetailScreen
import com.hapataka.questwalk.feature.quest.navigation.questScreen
import com.hapataka.questwalk.feature.record.navigation.recordScreen
import com.hapataka.questwalk.feature.splash.navigation.splashScreen
import com.hapataka.questwalk.feature.weather.navigation.weatherScreen

@Composable
internal fun MainNavHost(
    navigator: MainNavigator,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
        modifier = modifier,
    ) {
        // Splash
        splashScreen(
            onNavigateToHome = { navigator.navigateToHome(clearBackStack = true) },
            onNavigateToOnboarding = { navigator.navigateToOnboarding() },
        )

        // Onboarding (Login -> Join -> Setup -> Home)
        onboardingNavGraph(
            navigateToHome = {
                navigator.navigateToHome(clearBackStack = true)
            },
        )

        // Home
        homeScreen(
            onStartClick = { /* TODO: PlaySessionRepository.startSession */ },
            onStopClick = { /* TODO: PlaySessionRepository.stopSession */ },
            onCameraClick = { navigator.navigateToCamera() },
            onCompleteClick = { /* TODO: PlaySessionRepository.stopSession */ },
            onQuestChangeClick = { navigator.navigateToQuest() },
            onWeatherClick = { navigator.navigateToWeather() },
            onMyInfoClick = { navigator.navigateToMyInfo() },
            onRecordClick = { navigator.navigateToRecord() },
        )

        // Quest
        questScreen(
            onBackClick = { navigator.popBackStack() },
            onQuestDetailClick = { keyword -> navigator.navigateToQuestDetail(keyword) },
            onQuestSelected = { keyword ->
                // TODO: PlaySessionRepository.setKeyword
                navigator.popBackStack()
            },
        )

        questDetailScreen(
            onBackClick = { navigator.popBackStack() },
        )

        // Weather
        weatherScreen(
            onBackClick = { navigator.popBackStack() },
        )

        // Record
        recordScreen(
            onBackClick = { navigator.popBackStack() },
            onHistoryClick = { resultId ->
                navigator.navigateToResult(resultId)
            },
        )

        // MyInfo
        myInfoScreen(
            onBackClick = { navigator.popBackStack() },
            onLogoutSuccess = { navigator.navigateToOnboarding() },
        )

        // TODO: Camera, Result screens
    }
}
