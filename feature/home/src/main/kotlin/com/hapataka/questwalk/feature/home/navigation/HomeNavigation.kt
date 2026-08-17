package com.hapataka.questwalk.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.MainRoute
import com.hapataka.questwalk.feature.home.HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(MainRoute.Home, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onCameraClick: () -> Unit,
    onCompleteClick: (resultId: String) -> Unit,
    onQuestChangeClick: () -> Unit,
    onWeatherClick: () -> Unit,
    onMyInfoClick: () -> Unit,
    onRecordClick: () -> Unit,
) {
    composable<MainRoute.Home> {
        HomeRoute(
            onCameraClick = onCameraClick,
            onCompleteClick = onCompleteClick,
            onQuestChangeClick = onQuestChangeClick,
            onWeatherClick = onWeatherClick,
            onMyInfoClick = onMyInfoClick,
            onRecordClick = onRecordClick,
        )
    }
}
