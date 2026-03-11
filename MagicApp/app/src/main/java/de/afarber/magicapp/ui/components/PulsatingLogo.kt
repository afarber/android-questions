package de.afarber.magicapp.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun pulsatingLogo(
    pulseTriggerKey: Int,
    modifier: Modifier = Modifier,
) {
    val haloScale = remember { Animatable(1f) }
    val haloAlpha = remember { Animatable(0f) }

    LaunchedEffect(pulseTriggerKey) {
        repeat(5) {
            launch {
                haloScale.snapTo(1f)
                haloScale.animateTo(
                    targetValue = 2f,
                    animationSpec = tween(durationMillis = 600, easing = LinearEasing),
                )
            }
            launch {
                haloAlpha.snapTo(0.35f)
                haloAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 600, easing = LinearEasing),
                )
            }
        }
        haloScale.snapTo(1f)
        haloAlpha.snapTo(0f)
    }

    Box(
        modifier = modifier.size(44.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .graphicsLayer {
                        scaleX = haloScale.value
                        scaleY = haloScale.value
                        alpha = haloAlpha.value
                    }.clip(CircleShape)
                    .background(accentColor()),
        )
        Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentColor().copy(alpha = 0.25f))
                    .padding(6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor()),
            )
        }
    }
}
