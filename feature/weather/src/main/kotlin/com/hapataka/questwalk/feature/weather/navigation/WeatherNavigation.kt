package com.hapataka.questwalk.feature.weather.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.MainRoute
import com.hapataka.questwalk.feature.weather.WeatherRoute

fun NavController.navigateToWeather(navOptions: NavOptions? = null) {
    navigate(MainRoute.Weather, navOptions)
}

fun NavGraphBuilder.weatherScreen(
    onBackClick: () -> Unit,
) {
    composable<MainRoute.Weather> {
        WeatherRoute(
            onBackClick = onBackClick,
        )
    }
}
