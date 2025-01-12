package com.hapataka.questwalk.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.updateBounds
import com.hapataka.questwalk.core.designsystem.R
import com.hapataka.questwalk.core.designsystem.theme.MainPurple
import com.hapataka.questwalk.core.designsystem.theme.Surface1
import com.hapataka.questwalk.core.designsystem.theme.Typography

@Composable
fun PixelTextField(
    value: String,
    onValueChange: (String) -> Unit = {},
    hint: String? = null,
    label: String? = null,
    validator: ((String) -> Boolean)? = null,
    onErrorChange: (Boolean) -> Unit = {},
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    maxLine: Int = 1,
    modifier: Modifier = Modifier,
) {
    var isError by remember { mutableStateOf(false) }
    var isInitial by remember { mutableStateOf(false) }
    var maxHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(isError) { onErrorChange(isError) }

    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (isInitial) {
                    validator?.let { validate ->
                        if (it.hasFocus.not()) {
                            isError = validate(value)
                        }
                    }
                } else {
                    isInitial = true
                }
            },
        value = value,
        onValueChange = {
            onValueChange(it)
            isError = false
        },
        maxLines = maxLine,
        singleLine = maxLine == 1,
        cursorBrush = SolidColor(MainPurple),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        onTextLayout = {
            maxHeight = (it.lineCount * 48).dp
        },
        visualTransformation = if (keyboardOptions.keyboardType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None,
        textStyle = Typography.bodyLarge.copy(color = MainPurple),
        decorationBox = { innerTextField ->
            val bg = ContextCompat.getDrawable(LocalContext.current, R.drawable.bg_text_field)
                ?: return@BasicTextField

            Row(
                modifier = Modifier.run {
                    fillMaxWidth()
                        .heightIn(min = 56.dp, max = maxHeight)
                        .drawBehind {
                            bg.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
                            bg.draw(drawContext.canvas.nativeCanvas)
                        }
                        .padding(horizontal = 16.dp)
                },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leadingIcon?.let {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (isError) Color.Red else MainPurple
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        hint?.let { h ->
                            Text(
                                text = h,
                                color = if (isError) Color.Red else Surface1,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        label?.let { l ->
                            AnimatedVisibility(
                                visible = value.isNotEmpty(),
                                enter = slideInVertically() + fadeIn(),
                                exit = ExitTransition.None
                            ) {
                                Text(
                                    text = l,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isError) Color.Red else MainPurple,
                                )
                            }
                        }
                        innerTextField()
                    }
                }
                trailingIcon?.let {
                    Spacer(modifier = Modifier.size(16.dp))
                    trailingIcon()
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun PixelTextFieldPreview() {
    PixelTextField(
        value = "아이디를 입력해주세요."
    )
}