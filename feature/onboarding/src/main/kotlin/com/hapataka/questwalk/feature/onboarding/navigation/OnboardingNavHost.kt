package com.hapataka.questwalk.feature.onboarding.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.hapataka.questwalk.core.navigation.OnboardingStep
import com.hapataka.questwalk.feature.onboarding.screen.join.JoinRoute
import com.hapataka.questwalk.feature.onboarding.screen.login.LoginRoute
import com.hapataka.questwalk.feature.onboarding.screen.setup.SetupRoute

@Composable
internal fun OnboardingNavHost(
    startDestination: OnboardingStep = OnboardingStep.Login,
    navigateToHome: () -> Unit,
    padding: PaddingValues,
    navController: NavHostController = rememberNavController(),
) {
    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
            navController = navController,
            startDestination = startDestination,
        ) {
            val singleTopOptions = navOptions {
                launchSingleTop = true
            }

            composable<OnboardingStep.Login> {
                LoginRoute(
                    navigateToHome = navigateToHome,
                    navigateToSetup = {
                        navController.navigate(
                            OnboardingStep.Setup,
                            singleTopOptions
                        )
                    },
                    navigateToJoin = {
                        navController.navigate(
                            OnboardingStep.Join,
                            singleTopOptions
                        )
                    },
                )
            }

            composable<OnboardingStep.Setup> {
                SetupRoute(
                    navigateToLogin = {
                        navController.navigate(
                            route = OnboardingStep.Login,
                            navOptions = navOptions {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                            }
                        )
                    },
                    navigateToHome = navigateToHome,
                )
            }

            composable<OnboardingStep.Join> {
                JoinRoute(
                    popBackStack = navController::popBackStack
                )
            }
        }
    }
}