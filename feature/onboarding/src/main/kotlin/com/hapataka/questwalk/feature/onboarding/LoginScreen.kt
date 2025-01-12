package com.hapataka.questwalk.feature.onboarding

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.hapataka.questwalk.core.designsystem.component.PixelButton
import com.hapataka.questwalk.core.designsystem.component.PixelTextField
import com.hapataka.questwalk.core.designsystem.theme.HighLightYellow
import com.hapataka.questwalk.core.designsystem.theme.MainPurple
import com.hapataka.questwalk.core.designsystem.theme.Typography
import com.hapataka.questwalk.core.designsystem.theme.White60
import com.hapataka.questwalk.core.model.LoginState
import com.hapataka.questwalk.core.model.UserInfo
import com.hapataka.questwalk.feature.onboarding.component.LoginContent

@Composable
internal fun LoginRoute(
    navigateToHome: () -> Unit,
    navigateToSetup: () -> Unit,
    navigateToJoin: () -> Unit,
    padding: PaddingValues,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            when ((loginState as LoginState.Success).userInfo) {
                UserInfo.EXIST -> navigateToHome()
                UserInfo.NONE -> navigateToSetup()
            }
        }
    }

    LoginScreen(
        loginState = loginState,
        loginWithEmail = viewModel::loginWithEmail,
        navigateToJoin = navigateToJoin,
        padding = padding
    )
}

@Composable
private fun LoginScreen(
    loginState: LoginState = LoginState.Idle,
    loginWithEmail: (String, String) -> Unit = { _, _ -> },
    navigateToJoin: () -> Unit = {},
    padding: PaddingValues = PaddingValues(),
) {
    var id by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val (focusId, focusPassword) = remember { FocusRequester.createRefs() }

    LoginContent(
        modifier = Modifier
            .fillMaxSize()
            .background(MainPurple)
            .padding(top = padding.calculateTopPadding()),
    ) {
        val maxWidthModifier = Modifier.fillMaxWidth()

        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AsyncImage(
                model = R.drawable.img_title,
                contentDescription = null,
                modifier = maxWidthModifier.padding(top = 40.dp)
            )

            if (loginState is LoginState.Loading || (loginState is LoginState.Success && loginState.userInfo == UserInfo.EXIST)) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(52.dp)
                        .padding(top = 40.dp), color = HighLightYellow
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PixelTextField(
                        value = id,
                        onValueChange = { id = it },
                        hint = "아이디를 입력해 주세요",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusPassword.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusId)
                    )

                    PixelTextField(
                        value = password,
                        onValueChange = { password = it },
                        hint = "비밀번호를 입력해 주세요",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusPassword)
                    )
                }


                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PixelButton(
                        onClick = { loginWithEmail(id, password) },
                        text = "로그인",
                        modifier = maxWidthModifier,
                        enabled = id.isNotBlank() && password.isNotBlank()
                    )

                    TextButton(
                        onClick = navigateToJoin,
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
                        modifier = Modifier
                            .height(1.dp)
                            .weight(1f)
                            .background(White60)
                    )
                    Text(
                        style = Typography.labelLarge, text = "또는", color = White60
                    )
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .weight(1f)
                            .background(White60)
                    )
                }

                IconButton(
                    modifier = Modifier.size(52.dp),
                    onClick = { },
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
}


@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}