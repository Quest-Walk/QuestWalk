package com.hapataka.questwalk.feature.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hapataka.questwalk.core.designsystem.theme.MainPurple
import com.hapataka.questwalk.core.navigation.OnboardingStep
import com.hapataka.questwalk.core.ui.R
import com.hapataka.questwalk.core.ui.component.HorizontalScrollingBackground

@Composable
internal fun OnboardingRoute(
    navigateToHome: () -> Unit,
    padding: PaddingValues = PaddingValues(),
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val currentStep by viewModel.currentStep.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainPurple)
            .padding(top = padding.calculateTopPadding()),
        contentAlignment = Alignment.Center
    ) {
        HorizontalScrollingBackground(
            isAnimate = true,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .align(Alignment.TopCenter),
            imgId = R.drawable.bg_starts,
            duration = 300000
        )

        HorizontalScrollingBackground(
            isAnimate = true,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.22f)
                .align(Alignment.BottomCenter),
            imgId = R.drawable.bg_ground,
            duration = 160000
        )

        OnboardingScreen(
            currentStep = currentStep,
            navigateToLogin = { viewModel.setCurrentStep(OnboardingStep.Login) },
            navigateToJoin = { viewModel.setCurrentStep(OnboardingStep.Join) },
            navigateToSetup = { viewModel.setCurrentStep(OnboardingStep.Setup) },
            navigateToHome = navigateToHome,
            padding = padding,
        )
    }
}

@Composable
private fun OnboardingScreen(
    currentStep: OnboardingStep = OnboardingStep.Login,
    navigateToLogin: () -> Unit = {},
    navigateToJoin: () -> Unit = {},
    navigateToSetup: () -> Unit = {},
    navigateToHome: () -> Unit = {},
    padding: PaddingValues = PaddingValues(),
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = padding.calculateBottomPadding()),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = currentStep is OnboardingStep.Login,
            enter = slideInHorizontally(
                animationSpec = tween(500),
                initialOffsetX = { -(it + (it / 2)) }),
            exit = slideOutHorizontally(
                animationSpec = tween(500),
                targetOffsetX = { -(it + (it / 2)) })
        ) {
            LoginRoute(
                navigateToHome = navigateToHome,
                navigateToSetup = navigateToSetup,
                navigateToJoin = navigateToJoin,
            )
        }

        AnimatedVisibility(
            visible = currentStep is OnboardingStep.Join,
            enter = slideInHorizontally(
                animationSpec = tween(500),
                initialOffsetX = { it + (it / 2) }),
            exit = slideOutHorizontally(tween(500), targetOffsetX = { it + (it / 2) })
        ) {
            JoinRoute(
                navigateToLogin = navigateToLogin,
            )
        }

        AnimatedVisibility(
            visible = currentStep is OnboardingStep.Setup,
            enter = slideInHorizontally(
                animationSpec = tween(500),
                initialOffsetX = { it + (it / 2) }),
            exit = slideOutHorizontally(tween(500), targetOffsetX = { it + (it / 2) })
        ) {
            SetupRoute(
                navigateToLogin = navigateToLogin,
                navigateToHome = navigateToHome,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen()
}