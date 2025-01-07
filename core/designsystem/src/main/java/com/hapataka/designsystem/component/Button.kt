package com.hapataka.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.updateBounds
import com.hapataka.core.designsystem.R
import com.hapataka.designsystem.theme.ButtonBrown

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

    TextButton(
        onClick = onClick,
        modifier = modifier
            .background(color = Color.Transparent)
            .drawBehind {
                bg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
                bg?.draw(drawContext.canvas.nativeCanvas)
            },
        enabled = enabled,
    ) {
        Text(
            text = text,
            color = if (enabled) ButtonBrown else Color.White,
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