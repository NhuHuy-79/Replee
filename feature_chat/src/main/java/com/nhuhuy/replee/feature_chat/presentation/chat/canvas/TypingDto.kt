package com.nhuhuy.replee.feature_chat.presentation.chat.canvas

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.material.math.MathUtils.lerp

@Composable
fun TypingDotIndicator(
    modifier: Modifier = Modifier,
    dotColor: Color = Color(0xFF8E8E93),
    dotRadius: Dp = 4.dp,
    spacing: Dp = 6.dp,
    jumpHeight: Dp = 8.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(
        modifier = modifier
            .width((dotRadius * 6) + (spacing * 2))
            .height(dotRadius * 2 + jumpHeight)
    ) {
        val radiusPx = dotRadius.toPx()
        val spacingPx = spacing.toPx()
        val jumpHeightPx = jumpHeight.toPx()

        // Điểm bắt đầu x để 3 chấm nằm giữa Canvas
        val startX = radiusPx

        repeat(3) { index ->
            // 2. Tính toán độ lệch pha (Phase Shift) cho từng chấm
            // Mỗi chấm cách nhau một khoảng 0.5 radian để tạo hiệu ứng sóng
            val phaseShift = index * 0.5f

            // 3. Công thức hình Sin để tạo đường cong:
            // animationProgress * 2π đưa giá trị về vòng tròn lượng giác
            val fraction = ((animationProgress * 2f * Math.PI.toFloat()) + phaseShift)

            // Chúng ta dùng -sin vì trong Android tọa độ Y càng giảm thì càng lên cao
            val yOffset = kotlin.math.sin(fraction).coerceIn(-1f, 0f) * jumpHeightPx

            val x = startX + index * (radiusPx * 2 + spacingPx)
            val y = size.height - radiusPx + yOffset

            drawCircle(
                color = dotColor,
                radius = radiusPx,
                center = Offset(x, y),
                alpha = lerp(0.5f, 1f, -yOffset / jumpHeightPx)
            )
        }
    }
}

fun DrawScope.drawTypingDots(
    progress: Float,
    dotColor: Color,
    dotRadius: Float,
    spacing: Float,
    jumpHeight: Float
) {
    val totalWidth = (dotRadius * 2 * 3) + (spacing * 2)

    val startX = (size.width - totalWidth) / 2 + dotRadius

    val centerY = size.height / 2

    repeat(3) { index ->
        // Độ lệch pha để tạo hiệu ứng nảy lần lượt
        val phaseShift = index * 0.5f
        val fraction = ((progress * 2f * Math.PI.toFloat()) + phaseShift)

        // Tính toán nảy lên (chỉ lấy phần âm của Sin để nảy từ mặt đất lên)
        val yOffset = kotlin.math.sin(fraction).coerceIn(-1f, 0f) * jumpHeight

        // Tọa độ X của từng chấm
        val x = startX + index * (dotRadius * 2 + spacing)

        // Tọa độ Y: Lấy trung tâm Box làm mốc, nảy lên dựa trên yOffset
        // Chỉnh nhẹ (dotRadius / 2) để mắt người cảm thấy cân đối hơn
        val y = centerY + (dotRadius / 2) + yOffset

        drawCircle(
            color = dotColor,
            radius = dotRadius,
            center = Offset(x, y),
            alpha = (0.4f + (-yOffset / jumpHeight) * 0.6f).coerceIn(0f, 1f)
        )
    }
}