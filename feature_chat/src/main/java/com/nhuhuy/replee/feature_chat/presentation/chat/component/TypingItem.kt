package com.nhuhuy.replee.feature_chat.presentation.chat.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.animation.slideInVerticallyAnimation
import com.nhuhuy.replee.core.design_system.animation.slideOutVerticallyAnimation
import com.nhuhuy.replee.core.design_system.component.UserImage

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
                Icon(
                    imageVector = Icons.Rounded.MoreHoriz,
                    contentDescription = null,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }

        }
    }
}