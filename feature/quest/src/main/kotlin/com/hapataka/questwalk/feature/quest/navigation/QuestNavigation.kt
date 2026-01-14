package com.hapataka.questwalk.feature.quest.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.hapataka.questwalk.core.navigation.HomeRoute
import com.hapataka.questwalk.core.navigation.MainRoute
import com.hapataka.questwalk.feature.quest.QuestRoute
import com.hapataka.questwalk.feature.quest.detail.QuestDetailRoute

fun NavController.navigateToQuest(navOptions: NavOptions? = null) {
    navigate(MainRoute.Quest, navOptions)
}

fun NavController.navigateToQuestDetail(keyword: String, navOptions: NavOptions? = null) {
    navigate(HomeRoute.QuestDetail(keyword), navOptions)
}

fun NavGraphBuilder.questScreen(
    onBackClick: () -> Unit,
    onQuestDetailClick: (String) -> Unit,
    onQuestSelected: (String) -> Unit,
) {
    composable<MainRoute.Quest> {
        QuestRoute(
            onBackClick = onBackClick,
            onQuestDetailClick = onQuestDetailClick,
            onQuestSelected = onQuestSelected,
        )
    }
}

fun NavGraphBuilder.questDetailScreen(
    onBackClick: () -> Unit,
) {
    composable<HomeRoute.QuestDetail> {
        QuestDetailRoute(
            onBackClick = onBackClick,
        )
    }
}
