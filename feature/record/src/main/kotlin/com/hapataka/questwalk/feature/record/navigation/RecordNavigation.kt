package com.hapataka.questwalk.feature.record.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.MainRoute
import com.hapataka.questwalk.feature.record.RecordRoute

fun NavController.navigateToRecord(navOptions: NavOptions? = null) {
    navigate(MainRoute.Record, navOptions)
}

fun NavGraphBuilder.recordScreen(
    onBackClick: () -> Unit,
    onHistoryClick: (String) -> Unit,
) {
    composable<MainRoute.Record> {
        RecordRoute(
            onBackClick = onBackClick,
            onHistoryClick = onHistoryClick,
        )
    }
}
