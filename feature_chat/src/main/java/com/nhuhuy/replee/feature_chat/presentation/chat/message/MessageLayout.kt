package com.nhuhuy.replee.feature_chat.presentation.chat.message

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun MessageLayout(
    isMine: Boolean,
    showTimeContent: Boolean,
    userImage: @Composable () -> Unit,
    timeContent: @Composable () -> Unit,
    messageContent: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = showTimeContent
        ) {
            timeContent()
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
        ) {
            if (!isMine) {
                userImage()
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier
            ) {
                messageContent()
            }
        }
    }
}
