package com.hapataka.questwalk.feature.onboarding.component

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.hapataka.questwalk.core.designsystem.theme.MainPurple
import com.hapataka.questwalk.core.designsystem.theme.Surface1
import com.hapataka.questwalk.feature.onboarding.R

@Composable
internal fun PasswordVisibilityButton(
    isPasswordShow: Boolean,
    onChangePasswordVisibility: (Boolean) -> Unit,
) {
    Icon(
        imageVector = ImageVector.vectorResource(
            if (isPasswordShow) R.drawable.ic_password_hide else R.drawable.ic_password_show
        ),
        tint = if (isPasswordShow) MainPurple else Surface1,
        contentDescription = "비밀번호 표시 아이콘",
        modifier = Modifier
            .size(32.dp)
            .pointerInput(Unit) {
                awaitEachGesture {
                    while (true) {
                        val event = awaitPointerEvent()

                        event.changes.forEach { it.consume() }

                        when (event.type) {
                            PointerEventType.Press -> onChangePasswordVisibility(true)
                            PointerEventType.Release -> onChangePasswordVisibility(false)
                        }
                    }
                }
            }
    )
}