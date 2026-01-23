package com.hapataka.questwalk.feature.onboarding.screen.join

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hapataka.questwalk.core.designsystem.R.drawable
import com.hapataka.questwalk.core.designsystem.component.PixelButton
import com.hapataka.questwalk.core.designsystem.component.PixelTextField
import com.hapataka.questwalk.core.designsystem.component.QuestWalkTopAppBar
import com.hapataka.questwalk.core.designsystem.theme.HighLightYellow
import com.hapataka.questwalk.core.designsystem.theme.Typography
import com.hapataka.questwalk.feature.onboarding.component.PasswordVisibilityButton
import com.hapataka.questwalk.feature.onboarding.model.JoinState
import com.hapataka.questwalk.feature.onboarding.util.isEmailPattern
import com.hapataka.questwalk.feature.onboarding.util.isPasswordPattern

@Composable
internal fun JoinRoute(
    popBackStack: () -> Unit,
    navigateToSetup: () -> Unit,
    viewModel: JoinViewModel = hiltViewModel(),
) {
    val joinState by viewModel.joinState.collectAsStateWithLifecycle()

    LaunchedEffect(joinState) {
        if (joinState is JoinState.Success) navigateToSetup()
    }

    JoinScreen(
        joinState = joinState,
        joinWithEmail = viewModel::joinWithEmail,
        popBackStack = popBackStack,
    )
}

@Composable
internal fun JoinScreen(
    joinState: JoinState = JoinState.Idle,
    joinWithEmail: (String, String) -> Unit = { _, _ -> },
    popBackStack: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        QuestWalkTopAppBar(
            title = "회원가입",
            leadingIcon = ImageVector.vectorResource(drawable.ic_back),
            onClickLeadingIcon = popBackStack
        )

        when (joinState) {
            is JoinState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(52.dp)
                        .padding(top = 40.dp)
                        .align(Alignment.CenterHorizontally),
                    color = HighLightYellow,
                )
            }

            else -> {
                JoinContent(
                    joinWithEmail = joinWithEmail,
                    errorMessage = (joinState as? JoinState.Failure)?.message,
                )
            }
        }
    }
}

@Composable
fun JoinContent(
    joinWithEmail: (String, String) -> Unit = { _, _ -> },
    errorMessage: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .focusable()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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

        var isPasswordShow by remember { mutableStateOf(false) }
        var isConfirmShow by remember { mutableStateOf(false) }

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
            trailingIcon = {
                PasswordVisibilityButton(
                    isPasswordShow = isPasswordShow,
                    onChangePasswordVisibility = { isPasswordShow = it }
                )
            },
            onErrorChange = { passwordError = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPasswordShow) KeyboardType.Text else KeyboardType.Password,
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
            trailingIcon = {
                PasswordVisibilityButton(
                    isPasswordShow = isConfirmShow,
                    onChangePasswordVisibility = { isConfirmShow = it }
                )
            },
            onErrorChange = { confirmPasswordError = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isConfirmShow) KeyboardType.Text else KeyboardType.Password,
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
            onClick = {
                checkInputValidate()

                when {
                    emailError -> focusEmail.requestFocus()
                    passwordError -> focusPassword.requestFocus()
                    confirmPasswordError -> focusConfirmPassword.requestFocus()
                }
                focusManager.clearFocus()

                if (emailError.not() && passwordError.not() && confirmPasswordError.not()) {
                    joinWithEmail(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        ErrorMessage(
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            serverErrorMessage = errorMessage,
        )
    }
}

@Composable
private fun ErrorMessage(
    email: String,
    password: String,
    confirmPassword: String,
    emailError: Boolean,
    passwordError: Boolean,
    confirmPasswordError: Boolean,
    serverErrorMessage: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
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

        if (serverErrorMessage != null) {
            Text(
                text = "- $serverErrorMessage",
                style = Typography.labelLarge,
                color = HighLightYellow,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentHeight()
            )
        }
    }
}