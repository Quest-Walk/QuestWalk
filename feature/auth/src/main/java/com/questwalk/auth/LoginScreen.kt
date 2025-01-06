package com.questwalk.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hapataka.designsystem.component.HorizontalScrollingBackground
import com.hapataka.designsystem.component.PixelButton
import com.hapataka.designsystem.component.PixelTextField
import com.hapataka.designsystem.theme.MainPurple
import com.hapataka.designsystem.theme.Typography
import com.hapataka.designsystem.theme.White60
import com.hapataka.questwalk.feature.auth.R

@Composable
fun LoginRoute(
    padding: PaddingValues
) {
    LoginScreen(padding = padding)
}

@Composable
internal fun LoginScreen(
    padding: PaddingValues = PaddingValues()
) {
    LoginContent(
        modifier = Modifier.fillMaxSize().background(MainPurple)
            .padding(top = padding.calculateTopPadding()),
    ) {
        var id by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        val maxWidthModifier = Modifier.fillMaxWidth()

        Column(
            modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AsyncImage(
                model = R.drawable.img_title,
                contentDescription = null,
                modifier = maxWidthModifier.padding(top = 40.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PixelTextField(
                    value = id,
                    onValueChange = { id = it },
                    hint = "아이디를 입력해 주세요",
                    modifier = maxWidthModifier
                )

                PixelTextField(
                    value = password,
                    onValueChange = { password = it },
                    hint = "비밀번호를 입력해 주세요",
                    modifier = maxWidthModifier
                )
            }


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PixelButton(
                    onClick = {}, text = "로그인", modifier = maxWidthModifier, enabled = true
                )

                TextButton(
                    onClick = {},
                ) {
                    Text(
                        style = Typography.labelLarge, text = "회원가입", color = Color.White
                    )
                }
            }

            Row(
                modifier = maxWidthModifier,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.height(1.dp).weight(1f).background(White60)
                )
                Text(
                    style = Typography.labelLarge, text = "또는", color = White60
                )
                Box(
                    modifier = Modifier.height(1.dp).weight(1f).background(White60)
                )
            }

            IconButton(
                modifier = Modifier.size(52.dp),
                onClick = { /*TODO*/ },
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_google),
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.Unspecified,
                    contentDescription = "구글 로그인 버튼",
                )
            }
        }
    }
}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier, content: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier, contentAlignment = Alignment.Center
    ) {
        HorizontalScrollingBackground(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f).align(Alignment.TopCenter),
            imgId = R.drawable.bg_starts,
            duration = 300000
        )

        HorizontalScrollingBackground(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.22f).align(Alignment.BottomCenter),
            imgId = R.drawable.bg_ground,
            duration = 160000
        )

        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}
