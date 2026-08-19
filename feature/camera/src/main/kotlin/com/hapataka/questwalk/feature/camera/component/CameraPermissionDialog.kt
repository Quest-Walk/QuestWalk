package com.hapataka.questwalk.feature.camera.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CameraPermissionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "카메라 권한 필요")
        },
        text = {
            Text(text = "퀘스트 키워드를 촬영하려면 카메라 권한이 필요합니다.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("권한 요청")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        },
        modifier = modifier,
    )
}

@Composable
fun CameraPermissionDeniedDialog(
    onDismiss: () -> Unit,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "카메라 권한 거부됨")
        },
        text = {
            Text(text = "카메라 권한이 거부되었습니다. 설정에서 권한을 허용해주세요.")
        },
        confirmButton = {
            TextButton(onClick = onGoToSettings) {
                Text("설정으로 이동")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        },
        modifier = modifier,
    )
}
