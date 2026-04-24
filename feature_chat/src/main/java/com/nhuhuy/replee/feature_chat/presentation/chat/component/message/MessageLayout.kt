package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun MessageLayout(
    modifier: Modifier,
    isCurrentUser: Boolean,
    userImage: @Composable RowScope.() -> Unit,
    extraContent: @Composable ColumnScope.() -> Unit,
    messageContent: @Composable ColumnScope.() -> Unit,
    reactionContent: @Composable ColumnScope.() -> Unit,
    statusContent: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
            ) {
                if (!isCurrentUser) {
                    userImage()
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = if (!isCurrentUser) Alignment.Start else Alignment.End
                ) {
                    extraContent()
                    messageContent()
                    reactionContent()
                }

                if (isCurrentUser) {
                    Spacer(Modifier.width(4.dp))
                    statusContent()
                }
            }
        }

    }
}



