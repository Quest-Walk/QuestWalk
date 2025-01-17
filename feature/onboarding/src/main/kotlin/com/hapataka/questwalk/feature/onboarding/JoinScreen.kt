package com.hapataka.questwalk.feature.onboarding

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hapataka.questwalk.core.designsystem.R.drawable
import com.hapataka.questwalk.core.designsystem.component.PixelButton
import com.hapataka.questwalk.core.designsystem.component.PixelTextField
import com.hapataka.questwalk.core.designsystem.component.QuestWalkTopAppBar
import com.hapataka.questwalk.core.designsystem.theme.HighLightYellow
import com.hapataka.questwalk.core.designsystem.theme.Typography
import com.hapataka.questwalk.feature.onboarding.util.isEmailPattern
import com.hapataka.questwalk.feature.onboarding.util.isPasswordPattern

@Composable
internal fun JoinRoute(
    navigateToLogin: () -> Unit,
) {
    BackHandler(enabled = true) {
        navigateToLogin()
    }

    JoinScreen(
        navigateToLogin = navigateToLogin,
    )
}

@Composable
internal fun JoinScreen(
    navigateToLogin: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        QuestWalkTopAppBar(
            title = "회원가입",
            leadingIcon = ImageVector.vectorResource(drawable.ic_back),
            onClickLeadingIcon = navigateToLogin
        )

        JoinContent()
    }
}

@Composable
fun JoinContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .focusable()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val fillWidthModifier = Modifier.fillMaxWidth(0.8f)

        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var confirmPassword by rememberSaveable { mutableStateOf("") }

        var emailError by rememberSaveable { mutableStateOf(false) }
        var passwordError by rememberSaveable { mutableStateOf(false) }
        var confirmPasswordError by rememberSaveable { mutableStateOf(false) }

        val focusManager = LocalFocusManager.current
        val (focusEmail, focusPassword, focusConfirmPassword) = FocusRequester.createRefs()

        val checkInputValidate = {
            if (email.isNotBlank()) emailError = email.isEmailPattern().not()
            if (password.isNotBlank()) passwordError = password.isPasswordPattern().not()
            if (confirmPassword.isNotBlank()) confirmPasswordError = password != confirmPassword
        }

        PixelTextField(
            value = email,
            hint = "이메일을 입력해 주세요",
            onValueChange = { email = it.trim() },
            isError = emailError,
            validator = {
                checkInputValidate()
                if (it.isNotBlank()) emailError else true
            },
            onErrorChange = { emailError = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusPassword.requestFocus() }
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .focusRequester(focusEmail),
        )

        PixelTextField(
            value = password,
            hint = "비밀번호를 입력해 주세요",
            onValueChange = { password = it.trim() },
            isError = passwordError,
            validator = {
                checkInputValidate()
                if (it.isNotBlank()) passwordError else true
            },
            onErrorChange = { passwordError = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusConfirmPassword.requestFocus() }
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .focusRequester(focusPassword),
        )

        PixelTextField(
            value = confirmPassword,
            hint = "비밀번호를 확인해 주세요",
            onValueChange = { confirmPassword = it.trim() },
            isError = confirmPasswordError,
            validator = {
                checkInputValidate()
                if (it.isNotBlank()) confirmPasswordError else true
            },
            onErrorChange = { confirmPasswordError = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .focusRequester(focusConfirmPassword),
        )

        PixelButton(
            text = "가입하기",
            enabled = emailError.not() && passwordError.not() && confirmPasswordError.not() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank(),
            onClick = { focusManager.clearFocus() },
            modifier = fillWidthModifier
        )

        Log.e(
            "JoinScreen",
            "emailError: $emailError, passwordError: $passwordError, confirmPasswordError: $confirmPasswordError"
        )
        if (emailError) {
            if (email.isBlank()) {
                Text(
                    text = "- 이메일을 입력해 주세요",
                    style = Typography.labelLarge,
                    color = HighLightYellow,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .wrapContentHeight()
                )
            } else {
                Text(
                    text = "- 이메일 형식이 올바르지 않습니다",
                    style = Typography.labelLarge,
                    color = HighLightYellow,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .wrapContentHeight()
                )
            }
        }

        if (passwordError) {
            if (password.isBlank()) {
                Text(
                    text = "- 비밀번호를 입력해 주세요",
                    style = Typography.labelLarge,
                    color = HighLightYellow,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .wrapContentHeight()
                )
            } else {
                Text(
                    text = "- 비밀번호는 특수문자를 포함해 9자리 이상으로 만들어주세요",
                    style = Typography.labelLarge,
                    color = HighLightYellow,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .wrapContentHeight()
                )
            }
        }

        if (confirmPasswordError) {
            if (confirmPassword.isBlank()) {
                Text(
                    text = "- 비밀번호를 확인해 주세요",
                    style = Typography.labelLarge,
                    color = HighLightYellow,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .wrapContentHeight()
                )
            } else {
                Text(
                    text = "- 비밀번호가 일치하지 않습니다",
                    style = Typography.labelLarge,
                    color = HighLightYellow,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .wrapContentHeight()
                )
            }
        }
    }
}