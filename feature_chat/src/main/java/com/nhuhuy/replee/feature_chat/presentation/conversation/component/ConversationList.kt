package com.nhuhuy.replee.feature_chat.presentation.conversation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.domain.model.Conversation

@Composable
fun ConversationList(
    conversationList: List<Conversation>,
    onConversationClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
){
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!conversationList.isEmpty()){
            items(
                items = conversationList,
                key = { conversation -> conversation.id }
            ){ item ->
                ConversationItem(
                    conversation = item,
                    modifier = Modifier.fillMaxWidth()
                        .clickable{
                            onConversationClick(item.id)
                        }
                )
            }
        } else {
            item {
                Image(
                    painter = painterResource(R.drawable.bg_empty),
                    contentDescription = null
                )
            }

            item {
                Text(
                    text = stringResource(R.string.conversation_screen_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}


@Composable
fun ConversationItem(
    conversation: Conversation,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .height(84.dp)
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        ConversationUserImage(
            userName = conversation.members.first().name
        )
        ConversationLastMessage(
            userName = conversation.members.first().name,
            messageContent = conversation.lastMessageContent
        )

        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(
                8.dp)
        ) {
            Text(
                text = "4:30 PM",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Badge(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    text = "1",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}

@Composable
fun ConversationLastMessage(
    userName: String,
    messageContent: String,
){
    Column {
        Text(
            text = userName,
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = messageContent,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun ConversationUserImage(
    userName: String ,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier.size(56.dp).background(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = userName.toCharArray().first().uppercase(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}