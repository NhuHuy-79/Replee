package com.nhuhuy.replee.core.design_system.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AvatarContainer(
    sizeAvatar: Dp = 56.dp,
    angle: Float = 315f,
    colorDot: Color = MaterialTheme.colorScheme.tertiary,
    radiusDot: Float = 15f,
    showDot: Boolean = true,
    content: @Composable () -> Unit
) {

    val density = LocalDensity.current
    val outlineColor = MaterialTheme.colorScheme.background

    val radius = remember(sizeAvatar, density) {
        with(density) { sizeAvatar.toPx() / 2 }
    }

    val angleRad = remember(angle) {
        Math.toRadians(angle.toDouble()).toFloat()
    }

    Box(
        modifier = Modifier
            .size(sizeAvatar)
    ) {

        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
        ) {
            content()
        }

        if (showDot) {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {

                val offsetX = center.x + radius * cos(angleRad)
                val offsetY = center.y - radius * sin(angleRad)

                drawCircle(
                    color = outlineColor,
                    radius = radiusDot + 4f,
                    center = Offset(offsetX, offsetY)
                )

                drawCircle(
                    color = colorDot,
                    radius = radiusDot,
                    center = Offset(offsetX, offsetY)
                )
            }
        }
    }
}