package com.nhuhuy.replee.feature_chat.presentation.chat.component.emote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmoteFlowRow(
    onReactionClick: (reaction: String) -> Unit,
    modifier: Modifier = Modifier,
    reactions: Map<String, Int>,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
        modifier = modifier,
    ) {
        reactions.onEach { entry ->
            ReactionCard(
                reaction = entry.key,
                count = entry.value,
                onClick = onReactionClick
            )
        }
    }
}
