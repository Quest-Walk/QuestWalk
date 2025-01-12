package com.hapataka.questwalk.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
internal fun JoinRoute(
    padding: PaddingValues,
) {
    JoinScreen(
        padding = padding
    )
}

@Composable
private fun JoinScreen(
    padding: PaddingValues = PaddingValues(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = R.drawable.img_title,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(top = 40.dp)
        )
    }
}