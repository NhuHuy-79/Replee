@file:OptIn(ExperimentalMaterial3Api::class)

package com.nhuhuy.replee.feature_chat.presentation.conversation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationAction
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationState

@Composable
internal fun ConversationSuccessScreen(
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
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            ConversationSearchBar(
                userList = state.userList,
                expand = state.expandSearchBar,
                input = state.searchQuery,
                onSearch = {
                    onAction(ConversationAction.OnSearchBarClick)
                },
                onValueChange = { value ->
                    onAction(ConversationAction.OnQueryChange(value))
                },
                onExpandChange = { expand ->
                    onAction(ConversationAction.OnExpandChange(expand))
                },
            )

            ConversationList(
                conversationList = converationList,
                onConversationClick = { id ->
                    onAction(ConversationAction.OnConversationClick(id))
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun ConversationScreenPreview() {
    ConversationSuccessScreen(
        converationList = emptyList(),
        state = ConversationState(),
        onAction = {}
    )
}


