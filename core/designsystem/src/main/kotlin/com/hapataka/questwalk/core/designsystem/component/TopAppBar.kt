package com.hapataka.questwalk.core.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hapataka.questwalk.core.designsystem.R
import com.hapataka.questwalk.core.designsystem.theme.Typography

@Composable
fun QuestWalkTopAppBar(
    title: String,
    contentColor: Color = Color.White,
    leadingIcon: ImageVector? = null,
    onClickLeadingIcon: () -> Unit = {},
    trailingIcon: ImageVector? = null,
    onClickTrailingIcon: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.let {
            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp),
                onClick = onClickLeadingIcon
            ) {
                Icon(
                    modifier = Modifier
                        .width(16.dp),
                    imageVector = leadingIcon,
                    tint = contentColor,
                    contentDescription = stringResource(R.string.string_btn_back)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = title,
                color = contentColor,
                modifier = Modifier,
                style = Typography.titleLarge
            )
        }

        trailingIcon?.let {
            IconButton(
                modifier = Modifier.size(48.dp),
                onClick = onClickTrailingIcon
            ) {
                Icon(
                    modifier = Modifier.width(16.dp),
                    imageVector = trailingIcon,
                    tint = contentColor,
                    contentDescription = stringResource(R.string.string_btn_expand)
                )
            }
        }
    }
}