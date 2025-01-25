package com.hapataka.questwalk.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.updateBounds
import com.hapataka.questwalk.core.designsystem.R
import com.hapataka.questwalk.core.designsystem.theme.Typography

@Composable
fun PixelChipButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val context = LocalContext.current
    val bg = ContextCompat
        .getDrawable(
            context,
            R.drawable.bg_button_chip
        )

    Box(
        modifier = modifier
            .clickable(
                onClick = onClick
            )
            .drawBehind {
                bg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
                bg?.draw(drawContext.canvas.nativeCanvas)
            },
        contentAlignment = Alignment.Center,
    ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                ,
                text = text,
                color = Color.White,
                style = Typography.labelLarge
            )
    }
}

@Preview
@Composable
private fun PixelChipButtonPreview() {
    PixelChipButton(
        text = "퀘스트 변경",
        onClick = {},
    )
}