package com.hapataka.questwalk.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hapataka.questwalk.core.designsystem.theme.MainPurple
import com.hapataka.questwalk.core.navigation.OnboardingStep
import com.hapataka.questwalk.core.ui.R
import com.hapataka.questwalk.core.ui.component.HorizontalScrollingBackground
import com.hapataka.questwalk.feature.onboarding.navigation.OnboardingNavHost

@Composable
internal fun OnboardingScreen(
    isLoggedIn: Boolean = true,
    navigateToHome: () -> Unit = {},
    padding: PaddingValues = PaddingValues(),
) {
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

        OnboardingNavHost(
            startDestination = if (isLoggedIn) OnboardingStep.Setup else OnboardingStep.Login,
            padding = padding,
            navigateToHome = navigateToHome,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen()
}