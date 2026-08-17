package com.hapataka.questwalk.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hapataka.questwalk.core.designsystem.theme.Typography

private val Purple = Color(0xFF6B4EFF)

@Composable
fun StopPlayDialog(
    distance: Float,
    duration: String,
    step: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val message = if (distance > 0f) {
        "모험을 종료할까요?\n현재 기록: $duration · $step 걸음"
    } else {
        "지금 포기하면 이동한 거리가 너무 짧아서\n기록이 남지 않습니다.\n그래도 포기하시겠습니까?"
    }

    Dialog(
        onDismissRequest = { /* non-cancellable */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = message,
                style = Typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    )
                ) {
                    Text(
                        text = "취소",
                        color = Color.DarkGray
                    )
                }

                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple
                    )
                ) {
                    Text(
                        text = "확인",
                        color = Color.White
                    )
                }
            }
        }
    }
}
