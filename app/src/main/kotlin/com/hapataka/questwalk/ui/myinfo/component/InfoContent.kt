package com.hapataka.questwalk.ui.myinfo.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hapataka.questwalk.core.designsystem.theme.Black
import com.hapataka.questwalk.core.designsystem.theme.MainPurple
import com.hapataka.questwalk.core.designsystem.theme.Typography

@Composable
fun InfoContent(
    title: String,
    content: String,
    label: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = Typography.titleSmall,
            color = MainPurple,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = content,
                style = Typography.bodyLarge,
                color = Black,
            )

            label?.let {
                Text(
                    text = it,
                    style = Typography.labelMedium,
                    color = Black
                )
            }
        }
    }
}