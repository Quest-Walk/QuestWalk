package com.hapataka.questwalk.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
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
        // 화면 전환이 끝나기 전에 한 번 더 눌리면 두 번 pop 된다.
        // 홈까지 지워지면 그릴 목적지가 없어 흰 화면이 남는다
        if (!isCurrentEntryResumed()) return
        if (navController.previousBackStackEntry == null) return

        navController.popBackStack()
    }

    /** 전환 중에는 현재 목적지가 RESUMED가 아니므로 중복 입력을 걸러낼 수 있다. */
    private fun isCurrentEntryResumed(): Boolean {
        val entry = navController.currentBackStackEntry ?: return false
        return entry.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    }

    fun navigateToOnboarding(isLoggedIn: Boolean = false) {
        navController.navigate(Route.Onboarding(isLoggedIn = isLoggedIn)) {
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
