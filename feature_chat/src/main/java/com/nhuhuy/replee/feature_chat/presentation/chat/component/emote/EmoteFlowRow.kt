package com.nhuhuy.replee.feature_chat.presentation.chat.component.emote

import ReactionCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import basicReactions

@Preview(apiLevel = 35)
@Composable
fun EmoteFlowRow(
    modifier: Modifier = Modifier,
    reactions: List<String> = basicReactions,
) {
    FlowRow(
        horizontalArrangement = Arrangement.End,
        modifier = modifier,
    ) {
        reactions.forEach { reaction ->
            ReactionCard(
                reaction = reaction,
                count = 1,
                onClick = {}
            )
        }
    }
}