package com.hapataka.questwak.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.hapataka.questwalk.core.navigation.HomeRoute
import com.hapataka.questwalk.core.navigation.MainRoute
import com.hapataka.questwalk.core.navigation.OnboardingStep
import com.hapataka.questwalk.core.navigation.Route
import com.hapataka.questwalk.feature.onboarding.navigation.navigateOnboarding

class MainNavigator(
    val navController: NavHostController,
) {
    val startDestination = OnboardingStep.Login
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    private val singleTopOptions = navOptions {
        launchSingleTop = true
        restoreState = true
    }

    fun navigate(menu: Route) {
        when (menu) {
            is Route.Onboarding -> navController.navigateOnboarding(singleTopOptions)
            else -> throw IllegalArgumentException("존재하지 않는 메뉴입니다.")
        }
    }

    // MainRoute Navigation
    fun navigateToHome() {
        navController.navigate(MainRoute.Home, singleTopOptions)
    }

    fun navigateToQuest() {
        navController.navigate(MainRoute.Quest, singleTopOptions)
    }

    fun navigateToRecord() {
        navController.navigate(MainRoute.Record, singleTopOptions)
    }

    fun navigateToWeather() {
        navController.navigate(MainRoute.Weather, singleTopOptions)
    }

    fun navigateToMyInfo() {
        navController.navigate(MainRoute.MyInfo, singleTopOptions)
    }

    fun navigateToCamera() {
        navController.navigate(MainRoute.Camera, singleTopOptions)
    }

    // HomeRoute Navigation
    fun navigateToQuestDetail(keyword: String) {
        navController.navigate(HomeRoute.QuestDetail(keyword), singleTopOptions)
    }

    fun navigateToResult(resultId: String) {
        navController.navigate(HomeRoute.Result(resultId), singleTopOptions)
    }

    fun popBackStack() {
        navController.popBackStack()
    }
}

@Composable
fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}