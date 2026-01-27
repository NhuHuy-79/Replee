package com.nhuhuy.replee.feature_chat.presentation.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.component.AlertDialogContainer
import com.nhuhuy.replee.core.design_system.component.BoxContainer
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.presentation.setting.component.InformationUser
import com.nhuhuy.replee.feature_chat.presentation.setting.component.SecondaryOption
import com.nhuhuy.replee.feature_chat.presentation.setting.component.SecondaryOptionItem
import com.nhuhuy.replee.feature_chat.presentation.setting.component.SetNickNameSheet
import com.nhuhuy.replee.feature_chat.presentation.setting.component.ToggleableItem
import com.nhuhuy.replee.feature_chat.presentation.setting.state.OptionAction
import com.nhuhuy.replee.feature_chat.presentation.setting.state.OptionOverlay
import com.nhuhuy.replee.feature_chat.presentation.setting.state.OptionState

@Composable
fun OptionScreen(
    conversation: Conversation,
    state: OptionState,
    onAction: (OptionAction) -> Unit
) = BoxContainer {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            InformationTopBar(
                onBackPressed = {
                    onAction(OptionAction.OnBackPressed)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                InformationUser(
                    userName = state.otherUserName,
                    email = state.otherUserEmail,
                    modifier = Modifier
                )
            }

            item {
                ToggleableItem(
                    res = R.string.setting_pin_conversation,
                    subRes = R.string.setting_pin_conversation_sub,
                    checked = conversation.pinned,
                    onCheckedChange = { pinned ->
                        onAction(OptionAction.OnPin(pinned))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(
                        topEnd = 16.dp,
                        topStart = 16.dp,
                        bottomStart = 8.dp,
                        bottomEnd = 8.dp
                    )
                )
            }

            item {
                ToggleableItem(
                    res = R.string.setting_mute_conversation,
                    subRes = R.string.setting_mute_conversation_sub,
                    checked = conversation.muted,
                    onCheckedChange = { muted ->
                        onAction(OptionAction.OnMute(muted))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp,
                        topStart = 8.dp,
                        topEnd = 8.dp
                    )
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            val secondaryOptionList = SecondaryOption.entries.toList()

            items(
                count = secondaryOptionList.size,
                key = { index -> index }
            ) { index ->
                val lastItem = index == secondaryOptionList.lastIndex
                val firstItem = index == 0
                SecondaryOptionItem(
                    res = secondaryOptionList[index].label,
                    icon = secondaryOptionList[index].icon,
                    sub = secondaryOptionList[index].content,
                    shape = if (lastItem) RoundedCornerShape(
                        bottomEnd = 16.dp, bottomStart = 16.dp, topStart = 8.dp, topEnd = 8.dp
                    ) else if (firstItem) RoundedCornerShape(
                        topEnd = 16.dp, topStart = 16.dp, bottomEnd = 8.dp, bottomStart = 8.dp
                    )
                    else RoundedCornerShape(8.dp),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable {
                        onAction(OptionAction.OnSecondaryOptionSelect(secondaryOptionList[index]))
                    }
                )
            }
        }
    }

    when (state.overlay) {
        OptionOverlay.NONE -> Unit
        OptionOverlay.SET_NICK_NAME -> {
            SetNickNameSheet(
                ownerNickName = state.ownerNickName,
                onOwnerNameChange = { name -> onAction(OptionAction.OnOwnerNickNameChange(name)) },
                otherUserNickName = state.otherUserNickName,
                onOtherUserNameChange = { name -> onAction(OptionAction.OnOtherNickNameChange(name)) },
                onDismiss = { onAction(OptionAction.OnDismiss) },
                onConfirm = { onAction(OptionAction.OnOwnerNickNameSet) }
            )
        }

        OptionOverlay.DELETE_CHAT -> {
            AlertDialogContainer(
                onDismiss = {
                    onAction(OptionAction.OnDismiss)
                },
                onConfirm = {
                    onAction(OptionAction.OnConversationDelete)
                },
                title = R.string.dialog_delete_conversation,
                content = R.string.dialog_delete_conversation_content,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationTopBar(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onBackPressed
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null
                )
            }
        },
        title = {},
    )
}

