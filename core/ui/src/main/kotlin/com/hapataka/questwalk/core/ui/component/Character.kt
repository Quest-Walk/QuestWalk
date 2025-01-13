package com.hapataka.questwalk.core.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.painterResource
import com.hapataka.questwalk.core.ui.R

enum class Character(val images: List<Int>) {
    BEAR(
        listOf(
            R.drawable.img_bear_s,
            R.drawable.img_bear_l1,
            R.drawable.img_bear_l2,
            R.drawable.img_bear_l1,
            R.drawable.img_bear_s,
            R.drawable.img_bear_r1,
            R.drawable.img_bear_r2,
            R.drawable.img_bear_r1,
        )
    ),
}

@Composable
fun Character(
    character: Character = Character.BEAR,
    isAnimate: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val frame = character.images.map {
        painterResource(it)
    }

    val currentFrame by rememberInfiniteTransition(character.name).animateValue(
        initialValue = 0,
        targetValue = frame.size,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = character.name
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isAnimate) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(maxHeight)
                    .aspectRatio(0.939f)
                    .drawBehind {
                        with(frame[currentFrame]) {
                            draw(size = size)
                        }
                    },
            )
        } else {
            Image(
                painter = frame[0],
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(maxHeight)
                    .aspectRatio(0.939f)
            )
        }
    }
}