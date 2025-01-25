package com.hapataka.questwalk.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.updateBounds
import com.hapataka.questwalk.core.designsystem.R
import com.hapataka.questwalk.core.designsystem.theme.ButtonBrown

@Composable
fun PixelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String = ""
) {
    val context = LocalContext.current
    val bg = ContextCompat
        .getDrawable(
            context,
            if (enabled) R.drawable.bg_button_enable else R.drawable.bg_button_disable
        )

    Button(
        onClick = onClick,
        modifier = modifier
            .drawBehind {
                bg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
                bg?.draw(drawContext.canvas.nativeCanvas)
            },
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        enabled = enabled,
    ) {
        Text(
            text = text,
            color = if (enabled) ButtonBrown else Color.White,
        )
    }
}

@Composable
fun ImageButton(
    releasedPainterResource: Painter,
    contentDescription: String?,
    pressedPainterResource: Painter? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .background(color = Color.Transparent)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = interactionSource,
            ),
    ) {
        Image(
            painter = if (isPressed) pressedPainterResource
                ?: releasedPainterResource else releasedPainterResource,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(
    showBackground = true,
    name = "enable"
)
@Composable
private fun PixelButtonEnablePreview() {
    PixelButton(
        onClick = {},
        text = "테스트",
        enabled = true
    )
}

@Preview(
    showBackground = true,
    name = "disable"
)
@Composable
private fun PixelButtonDisablePreview() {
    PixelButton(
        onClick = {},
        text = "테스트",
        enabled = false
    )
}