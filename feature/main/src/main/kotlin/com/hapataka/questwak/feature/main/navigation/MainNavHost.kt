package com.hapataka.questwak.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.hapataka.questwalk.core.navigation.MainRoute
import com.hapataka.questwalk.core.navigation.Route
import com.hapataka.questwalk.feature.home.navigation.homeScreen
import com.hapataka.questwalk.feature.onboarding.navigation.onboardingNavGraph
import com.hapataka.questwalk.feature.quest.navigation.questDetailScreen
import com.hapataka.questwalk.feature.quest.navigation.questScreen
import com.hapataka.questwalk.feature.record.navigation.recordScreen
import com.hapataka.questwalk.feature.weather.navigation.weatherScreen
import com.hapataka.questwalk.feature.myinfo.navigation.myInfoScreen

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
        // Onboarding (Login -> Join -> Setup -> Home)
        onboardingNavGraph(
            isLoggedIn = navigator.isLoggedIn,
            navigateToHome = {
                navigator.navigateToHome(clearBackStack = true)
            },
        )

        // Home
        homeScreen(
            onStartClick = { /* TODO: MainViewModel.togglePlay */ },
            onStopClick = { /* TODO: MainViewModel.togglePlay */ },
            onCameraClick = { navigator.navigateToCamera() },
            onCompleteClick = { /* TODO: MainViewModel.togglePlay */ },
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
                // TODO: MainViewModel.setSelectKeyword(keyword)
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
