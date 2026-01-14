package com.hapataka.questwalk.feature.quest.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.MainRoute
import com.hapataka.questwalk.feature.quest.QuestRoute

fun NavController.navigateToQuest(navOptions: NavOptions? = null) {
    navigate(MainRoute.Quest, navOptions)
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
