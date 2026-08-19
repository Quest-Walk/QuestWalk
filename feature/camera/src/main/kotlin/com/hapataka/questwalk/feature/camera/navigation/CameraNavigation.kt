package com.hapataka.questwalk.feature.camera.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.MainRoute
import com.hapataka.questwalk.feature.camera.CameraRoute

fun NavController.navigateToCamera(navOptions: NavOptions? = null) {
    navigate(MainRoute.Camera, navOptions)
}

fun NavGraphBuilder.cameraScreen(
    onBackClick: () -> Unit,
    onQuestSuccess: () -> Unit,
    onQuestFailed: (message: String) -> Unit,
) {
    composable<MainRoute.Camera> {
        CameraRoute(
            onBackClick = onBackClick,
            onQuestSuccess = onQuestSuccess,
            onQuestFailed = onQuestFailed,
        )
    }
}
