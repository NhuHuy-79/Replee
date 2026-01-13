package com.nhuhuy.replee.feature_chat.presentation.conversation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.presentation.conversation.ConversationContent
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationAction
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationState

@Composable
fun ConversationScreen(
    state: ConversationState,
    conversationList: List<Conversation>,
    onAction: (ConversationAction) -> Unit,
){
    if (state.syncing && conversationList.isEmpty()){
        SyncingScreen(
            modifier = Modifier.fillMaxSize()
        )
    }

    else {
        ConversationContent(
            converationList = conversationList,
            state = state,
            onAction = onAction
        )
    }
}

@Preview
@Composable
fun SyncingScreen(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.bg_sync),
            contentDescription = null
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Syncing...",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}