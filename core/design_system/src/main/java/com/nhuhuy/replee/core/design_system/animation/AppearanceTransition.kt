package com.nhuhuy.replee.core.design_system.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

fun slideInVerticallyAnimation(
    duration: Int = 250,
): EnterTransition {
    return slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(duration))
}

fun slideOutVerticallyAnimation(
    duration: Int = 200,
): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(durationMillis = duration, easing = FastOutLinearInEasing)
    ) + fadeOut(animationSpec = tween(duration))
}