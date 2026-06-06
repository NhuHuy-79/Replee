package com.nhuhuy.replee.feature_chat.presentation.chat.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.core.presentation.animation.slideInVerticallyAnimation
import com.nhuhuy.replee.core.presentation.animation.slideOutVerticallyAnimation
import com.nhuhuy.replee.feature_chat.presentation.chat.canvas.drawTypingDots

@Composable
fun TypingAnimatedIndicator(
    visible: Boolean,
    name: String,
    imgUrl: String,
    modifier: Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val color = MaterialTheme.colorScheme.primary

    AnimatedVisibility(
        visible = visible,
        enter = slideInVerticallyAnimation(),
        exit = slideOutVerticallyAnimation()
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            UserImage(
                userName = name,
                photoUrl = imgUrl,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .size(width = 64.dp, height = 40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .drawWithCache {
                        val radiusPx = 4.dp.toPx()
                        val spacingPx = 4.dp.toPx()
                        val jumpHeightPx = 6.dp.toPx()

                        onDrawBehind {
                            drawTypingDots(
                                progress = progress,
                                dotColor = color,
                                dotRadius = radiusPx,
                                spacing = spacingPx,
                                jumpHeight = jumpHeightPx
                            )
                        }
                    }
            )
        }
    }
}

