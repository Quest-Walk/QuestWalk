package com.hapataka.questwalk.feature.main.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import com.hapataka.questwalk.feature.camera.navigation.cameraScreen
import com.hapataka.questwalk.feature.home.navigation.homeScreen
import com.hapataka.questwalk.feature.myinfo.navigation.myInfoScreen
import com.hapataka.questwalk.feature.onboarding.navigation.onboardingNavGraph
import com.hapataka.questwalk.feature.quest.navigation.questDetailScreen
import com.hapataka.questwalk.feature.quest.navigation.questScreen
import com.hapataka.questwalk.feature.record.navigation.recordScreen
import com.hapataka.questwalk.feature.result.navigation.resultScreen
import com.hapataka.questwalk.feature.splash.navigation.splashScreen
import com.hapataka.questwalk.feature.weather.navigation.weatherScreen

@Composable
internal fun MainNavHost(
    navigator: MainNavigator,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
        modifier = modifier,
    ) {
        // Splash
        splashScreen(
            onNavigateToHome = { navigator.navigateToHome(clearBackStack = true) },
            onNavigateToOnboarding = { isLoggedIn -> navigator.navigateToOnboarding(isLoggedIn) },
        )

        // Onboarding (Login -> Join -> Setup -> Home)
        onboardingNavGraph(
            navigateToHome = {
                navigator.navigateToHome(clearBackStack = true)
            },
        )

        // Home
        homeScreen(
            onCameraClick = { navigator.navigateToCamera() },
            onCompleteClick = { resultId -> navigator.navigateToResult(resultId) },
            onQuestChangeClick = { navigator.navigateToQuest() },
            onWeatherClick = { navigator.navigateToWeather() },
            onMyInfoClick = { navigator.navigateToMyInfo() },
            onRecordClick = { navigator.navigateToRecord() },
        )

        // Quest
        questScreen(
            onBackClick = { navigator.popBackStack() },
            onQuestDetailClick = { keyword -> navigator.navigateToQuestDetail(keyword) },
            onQuestSelected = {
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

        // Camera
        cameraScreen(
            onBackClick = { navigator.popBackStack() },
            onQuestSuccess = {
                navigator.popBackStack()
                Toast.makeText(context, "퀘스트 성공!", Toast.LENGTH_SHORT).show()
            },
            onQuestFailed = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
        )

        // Result
        resultScreen(
            onBackClick = { navigator.navigateToHome(clearBackStack = true) },
        )
    }
}
