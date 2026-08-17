package com.hapataka.questwalk.feature.onboarding.screen.setup

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hapataka.questwalk.core.designsystem.component.PixelButton
import com.hapataka.questwalk.core.designsystem.component.PixelTextField
import com.hapataka.questwalk.core.designsystem.theme.Typography
import com.hapataka.questwalk.core.model.CharacterType
import com.hapataka.questwalk.core.ui.component.Character
import com.hapataka.questwalk.feature.onboarding.model.UserState
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun SetupRoute(
    navigateToHome: () -> Unit,
    navigateToLogin: () -> Unit,
    setupViewModel: SetupViewModel = hiltViewModel(),
) {
    val focusManager = LocalFocusManager.current
    val keyboardOptions = remember {
        KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done)
    }
    val keyboardActions = remember {
        KeyboardActions(
            onDone = { focusManager.clearFocus() }
        )
    }

    val loginState by setupViewModel.loginState.collectAsState()

    BackHandler {
        setupViewModel.onIntent(SetupIntent.LogoutClicked)
    }

    LaunchedEffect(setupViewModel) {
        setupViewModel.event.collectLatest { event ->
            when (event) {
                SetupEvent.NavigateToHome -> navigateToHome()
                SetupEvent.NavigateToLogin -> navigateToLogin()
            }
        }
    }

    SetupScreen(
        ko = keyboardOptions,
        ka = keyboardActions,
        onIntent = setupViewModel::onIntent,
    )
}

@Composable
internal fun SetupScreen(
    ko: KeyboardOptions = KeyboardOptions.Default,
    ka: KeyboardActions = KeyboardActions.Default,
    onIntent: (SetupIntent) -> Unit = {},
) {
    var nickname by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .focusable(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Character(
            character = Character.BEAR,
            isAnimate = true,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f)
                .padding(top = 40.dp)
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
            onClick = { onIntent(SetupIntent.DoneClicked(nickname, CharacterType.BEAR)) },
            text = "완료",
            enabled = nickname.isNotBlank(),
            modifier = Modifier.fillMaxWidth(0.8f),
        )

        TextButton(
            onClick = {
                onIntent(SetupIntent.LogoutClicked)
                Log.d("logoutTest", "눌렸다")
            },
        ) {
            Text(
                style = Typography.labelLarge,
                text = "로그아웃", color =
                Color.White
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
)
@Composable
private fun SetupScreenPreview() {
    SetupScreen()
}
