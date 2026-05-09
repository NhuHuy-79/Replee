package com.nhuhuy.replee.feature_chat.presentation.chat.component.message_bubble

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessagePosition

@Composable
fun MessageBubbleLayout(
    showStatus: Boolean,
    isLastInGroup: Boolean,
    isLastInScreen: Boolean,
    position: MessagePosition,
    isReplyMessage: Boolean,
    isCurrentUser: Boolean,
    showReactions: Boolean,
    userImageContent: @Composable () -> Unit,
    timeStampContent: @Composable () -> Unit,
    statusContent: @Composable ColumnScope.() -> Unit,
    replyContent: @Composable (() -> Unit),
    mainContent: @Composable () -> Unit,
    reactionContent: @Composable ColumnScope.() -> Unit
) {
    val screenWidth = LocalWindowInfo.current.containerDpSize.width
    val bubbleMaxWidth = screenWidth * 0.8f
    val horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .widthIn(max = bubbleMaxWidth)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 150,
                    easing = FastOutSlowInEasing
                )
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = horizontalAlignment
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!isCurrentUser) {
                if (isLastInGroup) {
                    userImageContent()
                } else {
                    Spacer(modifier = Modifier.size(36.dp))
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = horizontalAlignment
            ) {
                if (position == MessagePosition.START || position == MessagePosition.SINGLE) {
                    timeStampContent()
                }
                if (isReplyMessage) {
                    replyContent()
                }
                mainContent()
            }
        }

        if (showReactions) {
            reactionContent()
        }

        if ((isCurrentUser && isLastInScreen) || showStatus) {
            statusContent()
        }
    }
}
