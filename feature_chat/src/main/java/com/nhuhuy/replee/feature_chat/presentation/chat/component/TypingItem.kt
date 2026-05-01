package com.nhuhuy.replee.feature_chat.presentation.chat.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.core.presentation.animation.slideInVerticallyAnimation
import com.nhuhuy.replee.core.presentation.animation.slideOutVerticallyAnimation
import com.nhuhuy.replee.feature_chat.R

@Composable
fun TypingItem(
    visible: Boolean,
    name: String,
    imgUrl: String,
    modifier: Modifier
) {
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
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                TypingAnimation()
            }

        }
    }
}

@Composable
private fun TypingAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.typing_indicator)
    )
    LottieAnimation(
        composition = composition,
        modifier = modifier.width(56.dp),
        iterations = Int.MAX_VALUE,
    )
}
