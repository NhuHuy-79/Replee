@file:OptIn(ExperimentalMaterial3Api::class)

package com.nhuhuy.replee.feature_chat.presentation.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.component.BoxContainer
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.presentation.conversation.component.ConversationList
import com.nhuhuy.replee.feature_chat.presentation.conversation.component.ConversationSearchBar
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationAction
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationState

@Composable
internal fun ConversationContent(
    converationList: List<Conversation>,
    state: ConversationState,
    onAction: (ConversationAction) -> Unit
) = BoxContainer {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
            ) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = null
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surface
                )
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            ConversationSearchBar(
                currentUser = state.currentUser,
                state = state.searchState,
                expand = state.expandSearchBar,
                input = state.searchQuery,
                onSearch = {
                    onAction(ConversationAction.OnSearch)
                },
                onValueChange = { value ->
                    onAction(ConversationAction.OnQueryChange(value))
                },
                onExpandChange = { expand ->
                    onAction(ConversationAction.OnExpandChange(expand))
                },
                onAvatarClick = { account ->
                    onAction(ConversationAction.OnAvatarClick(account = account))
                },
                goToProfile = {
                    onAction(ConversationAction.OnOwnerClick)
                }
            )

            ConversationList(
                conversationList = converationList,
                onConversationClick = { conversation ->
                    onAction(ConversationAction.OnConversationClick(conversation))
                },
                modifier = Modifier.weight(1f)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Preview
@Composable
fun ConversationScreenPreview() {
    ConversationContent(
        converationList = emptyList(),
        state = ConversationState(),
        onAction = {}
    )
}


