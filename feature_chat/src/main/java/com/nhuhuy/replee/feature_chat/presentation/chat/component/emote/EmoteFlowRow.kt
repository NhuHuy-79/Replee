package com.nhuhuy.replee.feature_chat.presentation.chat.component.emote

import ReactionCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import basicReactions

@Composable
fun EmoteFlowRow(
    onReactionClick: (reaction: String) -> Unit,
    modifier: Modifier = Modifier,
    reactions: List<String> = basicReactions,
) {
    FlowRow(
        horizontalArrangement = Arrangement.End,
        modifier = modifier,
    ) {
        reactions.toSet().forEach { reaction ->
            ReactionCard(
                reaction = reaction,
                count = 1,
                onClick = onReactionClick
            )
        }
    }
}