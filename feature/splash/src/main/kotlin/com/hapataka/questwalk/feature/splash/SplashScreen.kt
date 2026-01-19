package com.hapataka.questwalk.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hapataka.questwalk.core.designsystem.R
import kotlinx.coroutines.launch

private val MainPurple = Color(0xFF7A5EC8)

@Composable
internal fun SplashScreen() {
    // Title: alpha 0->1, scale 0.97->1 at 20% (400ms)
    val titleAlpha = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0.97f) }

    // Stars: alpha 0->0.5 at 20% (400ms), alpha 0.5->1 + translationY -40->0 at 55% (1100ms)
    val starsAlpha = remember { Animatable(0f) }
    val starsOffset = remember { Animatable(-40f) }

    // Ground: alpha 0->1 + translationY 60->0 at 50% (1000ms)
    val groundAlpha = remember { Animatable(0f) }
    val groundOffset = remember { Animatable(60f) }

    LaunchedEffect(Unit) {
        // Title: alpha 0->1 at 20% (400ms), scale 0.97->1 over 2000ms
        launch {
            titleAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = LinearEasing)
            )
        }
        launch {
            titleScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
            )
        }

        // Stars: alpha 0->0.5 at 20% (400ms), then alpha 0.5->1 + offset -40->0 at 55% (700ms more)
        launch {
            starsAlpha.animateTo(
                targetValue = 0.5f,
                animationSpec = tween(durationMillis = 400, easing = LinearEasing)
            )
            starsAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = LinearEasing)
            )
        }
        launch {
            starsOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1100, easing = LinearEasing)
            )
        }

        // Ground: alpha + offset at 50% (1000ms)
        launch {
            groundAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
        }
        launch {
            groundOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainPurple),
    ) {
        // Stars (top)
        Image(
            painter = painterResource(id = R.drawable.image_stars),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .offset(y = starsOffset.value.dp)
                .alpha(starsAlpha.value),
            contentScale = ContentScale.Crop,
        )

        // Title (top with padding)
        Image(
            painter = painterResource(id = R.drawable.image_splash_title),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
                .scale(titleScale.value)
                .alpha(titleAlpha.value),
            contentScale = ContentScale.Crop,
        )

        // Ground (bottom)
        Image(
            painter = painterResource(id = R.drawable.image_ground),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset(y = groundOffset.value.dp)
                .alpha(groundAlpha.value),
            contentScale = ContentScale.FillWidth,
        )
    }
}
