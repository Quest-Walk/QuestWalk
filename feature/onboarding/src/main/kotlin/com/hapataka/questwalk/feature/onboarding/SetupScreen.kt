package com.hapataka.questwalk.feature.onboarding

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hapataka.questwalk.core.designsystem.component.Character
import com.hapataka.questwalk.core.designsystem.component.PixelButton
import com.hapataka.questwalk.core.designsystem.component.PixelTextField
import com.hapataka.questwalk.core.designsystem.theme.HighLightYellow
import com.hapataka.questwalk.core.designsystem.theme.MainPurple
import com.hapataka.questwalk.core.designsystem.theme.Typography
import com.hapataka.questwalk.core.model.CharacterType
import com.hapataka.questwalk.core.model.LoginState
import com.hapataka.questwalk.feature.onboarding.component.SetupContent

@Composable
internal fun SetupRoute(
    navigateToHome: () -> Unit,
    setupViewModel: SetupViewModel = hiltViewModel(),
    padding: PaddingValues,
) {
    val focusManager = LocalFocusManager.current
    val keyboardOptions = remember {
        KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done)
    }
    val keyboardActions = remember {
        KeyboardActions(
            onDone = {
                Log.d("loginFocusTest", "onDoneClick")
                focusManager.clearFocus()
            }
        )
    }
    val loginState by setupViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            navigateToHome()
        }
    }

    SetupScreen(
        ko = keyboardOptions,
        ka = keyboardActions,
        postUserInfo = setupViewModel::postUserInfo,
        padding = padding
    )
}

@Composable
private fun SetupScreen(
    ko: KeyboardOptions = KeyboardOptions.Default,
    ka: KeyboardActions = KeyboardActions.Default,
    postUserInfo: (String, CharacterType) -> Unit = { _, _ -> },
    padding: PaddingValues = PaddingValues(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var nickname by rememberSaveable { mutableStateOf("") }
    var characterType by rememberSaveable { mutableStateOf(CharacterType.BEAR) }
    val focusRequester = remember { FocusRequester() }


    SetupContent(
        modifier = Modifier
            .fillMaxSize()
            .background(MainPurple)
            .padding(top = padding.calculateTopPadding())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "캐릭터를 선택해 주세요",
                style = Typography.bodyLarge,
                color = HighLightYellow,
                modifier = Modifier
                    .padding(top = 40.dp)
            )

            Character(
                character = when (characterType) {
                    CharacterType.BEAR -> Character.BEAR
                    else -> Character.BEAR
                },
                isAnimate = true,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
            )

            PixelTextField(
                value = nickname,
                onValueChange = { nickname = it.trim() },
                hint = "닉네임을 입력해 주세요",
                keyboardOptions = ko,
                keyboardActions = ka,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .onFocusChanged { focusState ->
                        Log.d("FocusState", "Focused: ${focusState.isFocused}")
                    }
                    .focusRequester(focusRequester)
            )


            PixelButton(
                onClick = { postUserInfo(nickname, characterType) },
                text = "완료",
                enabled = nickname.isNotBlank(),
                modifier = Modifier.fillMaxWidth(0.8f),
            )
        }
    }

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun SetupScreenPreview() {
    SetupScreen()
}