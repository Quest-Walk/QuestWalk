package com.hapataka.questwalk.feature.camera.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

private val Yellow = Color(0xFFFFFF00)

@Composable
fun CropFrame(
    modifier: Modifier = Modifier,
    frameRatio: Float = 0.75f,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val frameSize = size.width * frameRatio
        val left = (size.width - frameSize) / 2
        val top = (size.height - frameSize) / 2
        val cornerRadius = 15.dp.toPx()
        val strokeWidth = 4.dp.toPx()

        // Semi-transparent overlay
        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            size = size,
        )

        // Clear the crop area (rounded rectangle)
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(frameSize, frameSize),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            blendMode = BlendMode.Clear,
        )

        // Yellow border for crop area (rounded rectangle)
        drawRoundRect(
            color = Yellow,
            topLeft = Offset(left, top),
            size = Size(frameSize, frameSize),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            style = Stroke(width = strokeWidth),
        )
    }
}
