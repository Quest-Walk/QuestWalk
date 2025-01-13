package com.hapataka.questwalk.core.ui.component

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalScrollingBackground(
    isAnimate: Boolean = false,
    modifier: Modifier = Modifier,
    @DrawableRes imgId: Int, duration: Int = 1000,
) {
    val bitmapResource = ImageBitmap.imageResource(imgId)
    var containerWidth by remember { mutableFloatStateOf(0f) }
    var containerHeight by remember { mutableFloatStateOf(0f) }

    Box(modifier = modifier.onSizeChanged {
        containerWidth = it.width.toFloat()
        containerHeight = it.height.toFloat()
    }) {
        // 높이를 기준으로 이미지를 확대할 비율 계산
        val scale = containerHeight / bitmapResource.height
        // 비율을 기준으로 배경 이미지 리사이징
        val backgroundImage = bitmapResource.resize(
            (bitmapResource.width * scale).toInt() + 1,
            (bitmapResource.height * scale).toInt() + 1,
        )
        // 애니메이션 설정

        var lastPosition by remember { mutableFloatStateOf(0f) }
        val infiniteTransition = rememberInfiniteTransition(label = "background")
        val offset by if (isAnimate) infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = backgroundImage.width.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = duration, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "background"
        ) else remember { mutableFloatStateOf(0f) }


        val imageCount = ((containerWidth / backgroundImage.width).toInt() + 2)

        Box(modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = -offset % backgroundImage.width
            }
        ) {
            Box(modifier = Modifier
                .fillMaxHeight()
                .width(((backgroundImage.width - 1) * 2).dp)
                .drawBehind {
                    for (i in 0 until imageCount) {
                        drawImage(
                            image = backgroundImage, topLeft = Offset(
                                x = i * (backgroundImage.width - 1).toFloat(), y = 0f
                            )
                        )
                    }
                })
        }
    }
}

fun ImageBitmap.resize(newWidth: Int, newHeight: Int): ImageBitmap {
    val prev = Bitmap.createBitmap(this.asAndroidBitmap())

    this.prepareToDraw()
    Bitmap.createScaledBitmap(prev, newWidth, newHeight, true).let {
        return it.asImageBitmap()
    }
}
