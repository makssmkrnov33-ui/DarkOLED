package com.darkoled.app.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale

fun Modifier.bounceOnPress(
    interactionSource: MutableInteractionSource? = null,
    scaleIn: Float = 0.85f
): Modifier = composed {
    val src = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by src.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleIn else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "bounce"
    )
    this then Modifier.scale(scale)
}
