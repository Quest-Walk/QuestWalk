package com.hapataka.questwalk.feature.onboarding.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil3.compose.AsyncImage
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.hapataka.questwalk.core.designsystem.component.PixelButton
import com.hapataka.questwalk.core.designsystem.component.PixelTextField
import com.hapataka.questwalk.core.designsystem.theme.HighLightYellow
import com.hapataka.questwalk.core.designsystem.theme.Typography
import com.hapataka.questwalk.core.designsystem.theme.White60
import com.hapataka.questwalk.feature.onboarding.R
import com.hapataka.questwalk.feature.onboarding.component.PasswordVisibilityButton
import com.hapataka.questwalk.feature.onboarding.model.UserInfo
import com.hapataka.questwalk.feature.onboarding.model.UserState
import com.hapataka.questwalk.feature.onboarding.util.getCredential
import kotlinx.coroutines.launch

@Composable
internal fun LoginRoute(
    navigateToHome: () -> Unit,
    navigateToSetup: () -> Unit,
    navigateToJoin: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val userState by viewModel.userState.collectAsState()

    LaunchedEffect(userState) {
        if (userState is UserState.LoggedIn) {
            when ((userState as UserState.LoggedIn).userInfo) {
                UserInfo.EXIST -> navigateToHome()
                UserInfo.NONE -> navigateToSetup()
            }
        }
    }

    LoginScreen(
        userState = userState,
        loginWithEmail = viewModel::loginWithEmail,
        loginWithIdToken = viewModel::loginWithIdToken,
        navigateToJoin = navigateToJoin,
    )
}

@Composable
internal fun LoginScreen(
    userState: UserState = UserState.Idle,
    loginWithEmail: (String, String) -> Unit = { _, _ -> },
    loginWithIdToken: (String) -> Unit = { _ -> },
    navigateToJoin: () -> Unit = {},
) {
    var id by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val (focusId, focusPassword) = remember { FocusRequester.createRefs() }

    var isPasswordShow by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .focusable(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AsyncImage(
            model = R.drawable.img_title,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
        )

        if (userState is UserState.Loading || (userState is UserState.LoggedIn && userState.userInfo == UserInfo.EXIST)) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(52.dp)
                    .padding(top = 40.dp), color = HighLightYellow
            )
        } else {
            val maxWidthModifier = Modifier.fillMaxWidth()

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
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
                    modifier = maxWidthModifier
                        .focusRequester(focusId)
                )

                PixelTextField(
                    value = password,
                    onValueChange = { password = it },
                    hint = "비밀번호를 입력해 주세요",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (isPasswordShow) KeyboardType.Text else KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    trailingIcon = {
                        PasswordVisibilityButton(
                            isPasswordShow = isPasswordShow,
                            onChangePasswordVisibility = { isPasswordShow = it }
                        )
                    },
                    modifier = maxWidthModifier
                        .focusRequester(focusPassword)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PixelButton(
                    onClick = { loginWithEmail(id, password) },
                    text = "로그인",
                    modifier = Modifier.fillMaxWidth(),
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
                onClick = {
                    lifecycleOwner.lifecycleScope.launch {
                        getCredential(context)
                            .onSuccess {
                                when (it.type) {
                                    GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                                        val idToken =
                                            GoogleIdTokenCredential.createFrom(it.data).idToken

                                        loginWithIdToken(idToken)
                                    }

                                    else -> {}
                                }
                            }
                            .onFailure {
                                // TODO: 구글 인증정보 가져오기 실패 구현
                            }
                    }
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_google),
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.Unspecified,
                    contentDescription = "구글 로그인 버튼",
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}