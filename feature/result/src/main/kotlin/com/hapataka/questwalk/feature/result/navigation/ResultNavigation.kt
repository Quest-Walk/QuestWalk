package com.hapataka.questwalk.feature.result.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.HomeRoute
import com.hapataka.questwalk.feature.result.ResultRoute

fun NavController.navigateToResult(resultId: String, navOptions: NavOptions? = null) {
    navigate(HomeRoute.Result(resultId), navOptions)
}

fun NavGraphBuilder.resultScreen(
    onBackClick: () -> Unit,
) {
    composable<HomeRoute.Result> {
        ResultRoute(
            onBackClick = onBackClick,
        )
    }
}
