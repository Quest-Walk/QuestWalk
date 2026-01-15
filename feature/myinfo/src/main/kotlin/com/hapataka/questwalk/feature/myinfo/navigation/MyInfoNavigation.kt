package com.hapataka.questwalk.feature.myinfo.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.MainRoute
import com.hapataka.questwalk.feature.myinfo.MyInfoRoute

fun NavController.navigateToMyInfo(navOptions: NavOptions? = null) {
    navigate(MainRoute.MyInfo, navOptions)
}

fun NavGraphBuilder.myInfoScreen(
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
) {
    composable<MainRoute.MyInfo> {
        MyInfoRoute(
            onBackClick = onBackClick,
            onLogoutSuccess = onLogoutSuccess,
        )
    }
}
