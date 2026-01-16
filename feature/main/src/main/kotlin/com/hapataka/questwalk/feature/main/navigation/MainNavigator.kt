package com.hapataka.questwalk.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.hapataka.questwalk.core.navigation.HomeRoute
import com.hapataka.questwalk.core.navigation.MainRoute
import com.hapataka.questwalk.core.navigation.Route

class MainNavigator(
    val navController: NavHostController,
) {
    val startDestination: Any = Route.Splash

    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    private val singleTopOptions = navOptions {
        launchSingleTop = true
        restoreState = true
    }

    // Main Navigation
    fun navigateToHome(clearBackStack: Boolean = false) {
        if (clearBackStack) {
            navController.navigate(MainRoute.Home) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate(MainRoute.Home, singleTopOptions)
        }
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

    // Detail Navigation
    fun navigateToQuestDetail(keyword: String) {
        navController.navigate(HomeRoute.QuestDetail(keyword), singleTopOptions)
    }

    fun navigateToResult(resultId: String) {
        navController.navigate(HomeRoute.Result(resultId), singleTopOptions)
    }

    fun popBackStack() {
        navController.popBackStack()
    }

    fun navigateToOnboarding() {
        navController.navigate(Route.Onboarding) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }
}

@Composable
fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}
