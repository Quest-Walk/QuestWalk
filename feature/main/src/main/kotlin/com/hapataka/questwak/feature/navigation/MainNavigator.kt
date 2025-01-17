package com.hapataka.questwak.feature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
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
            is Route.Onboarding -> navController.navigateOnboarding(
                menu.onboardingStep,
                singleTopOptions
            )
//            is OnboardingStep.Login -> navController.navigateLogin(navOptions = singleTopOptions)
//            is OnboardingStep.Join -> navController.navigateJoin(navOptions = singleTopOptions)
//            is OnboardingStep.Setup -> navController.navigateSetup(navOptions = singleTopOptions)
            else -> throw IllegalArgumentException("존재하지 않는 메뉴입니다.")
        }
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