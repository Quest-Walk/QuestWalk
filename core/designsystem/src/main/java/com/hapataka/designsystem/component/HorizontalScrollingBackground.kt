package com.hapataka.designsystem.component

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.imageResource

@Composable
fun HorizontalScrollingBackground(
    modifier: Modifier = Modifier, @DrawableRes imgId: Int, duration: Int = 1000
) {
    val background = ImageBitmap.imageResource(imgId)
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    var targetValue by remember { mutableFloatStateOf(0f) }
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = targetValue, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "background"
    )

    Box(
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val scale = size.height / background.height
            val (scaleWidth, scaleHeight) = background.width * scale to background.height * scale
            val imagesCount = (size.width / scaleWidth).toInt() + 2

            targetValue = scaleWidth

            translate(left = -offset % scaleWidth) {
                for (x in 0 until imagesCount) {
                    drawImage(
                        image = background.resize(scaleWidth.toInt() + 1, scaleHeight.toInt() + 1),
                        topLeft = Offset(
                            x = x * scaleWidth, y = 0f
                        ),
                    )
                }
            }
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
