package com.hapataka.questwalk.feature.home.navigation

import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.MainRoute
import com.hapataka.questwalk.feature.home.HomeRoute
import com.hapataka.questwalk.feature.home.HomeUiState

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(MainRoute.Home, navOptions)
}

fun NavGraphBuilder.homeScreen(
    uiState: HomeUiState,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onCameraClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onQuestChangeClick: () -> Unit,
    onWeatherClick: () -> Unit,
    onMyInfoClick: () -> Unit,
    onRecordClick: () -> Unit,
) {
    composable<MainRoute.Home> {
        HomeRoute(
            uiState = uiState,
            onStartClick = onStartClick,
            onStopClick = onStopClick,
            onCameraClick = onCameraClick,
            onCompleteClick = onCompleteClick,
            onQuestChangeClick = onQuestChangeClick,
            onWeatherClick = onWeatherClick,
            onMyInfoClick = onMyInfoClick,
            onRecordClick = onRecordClick,
        )
    }
}
