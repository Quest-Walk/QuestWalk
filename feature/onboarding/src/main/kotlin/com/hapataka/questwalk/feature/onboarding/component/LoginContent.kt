package com.hapataka.questwalk.feature.onboarding.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hapataka.questwalk.core.ui.R
import com.hapataka.questwalk.core.ui.component.HorizontalScrollingBackground

@Composable
internal fun LoginContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier, contentAlignment = Alignment.Center
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

        content()
    }
}