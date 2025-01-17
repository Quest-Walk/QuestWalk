package com.hapataka.questwalk.feature.onboarding.navigation

import android.os.Bundle
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.hapataka.questwalk.core.navigation.OnboardingStep
import com.hapataka.questwalk.core.navigation.Route
import com.hapataka.questwalk.feature.onboarding.OnboardingRoute
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

val OnboardingStepType = object : NavType<OnboardingStep>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): OnboardingStep? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): OnboardingStep {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: OnboardingStep) {
        bundle.putString(key, Json.encodeToString(OnboardingStep.serializer(), value))
    }

    override fun serializeAsValue(value: OnboardingStep): String {
        return Json.encodeToString(OnboardingStep.serializer(), value)
    }
}

fun NavController.navigateOnboarding(route: OnboardingStep, navOptions: NavOptions) {
    navigate(Route.Onboarding(route), navOptions)
}

fun NavGraphBuilder.onboardingNavGraph(
    navigateToHome: () -> Unit,
    padding: PaddingValues,
) {
    composable<Route.Onboarding>(
        typeMap = mapOf(typeOf<OnboardingStep>() to OnboardingStepType)
    ) {
        OnboardingRoute(
            navigateToHome = navigateToHome,
            padding = padding
        )
    }
}