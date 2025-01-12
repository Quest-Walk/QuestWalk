package com.hapataka.questwalk.feature.onboarding.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hapataka.questwalk.core.designsystem.component.HorizontalScrollingBackground
import com.hapataka.questwalk.feature.onboarding.R

@Composable
internal fun SetupContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier, contentAlignment = Alignment.Center
    ) {
        HorizontalScrollingBackground(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f).align(Alignment.TopCenter),
            imgId = R.drawable.bg_starts,
            duration = 300000
        )

        content()
    }
}