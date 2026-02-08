package com.nhuhuy.replee.feature_chat.presentation.conversation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.core.design_system.state.ScreenStateHost
import com.nhuhuy.replee.feature_chat.R
import java.util.Locale

@ExperimentalMaterial3Api
@Composable
fun ConversationSearchBar(
    currentUser: Account,
    state: ScreenState<List<Account>>,
    expand: Boolean,
    input: String,
    onAvatarClick: (account: Account) -> Unit,
    goToProfile: () -> Unit,
    onSearch: (String) -> Unit,
    onValueChange: (String) -> Unit,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
){
    TextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                modifier = modifier,
                query = input,
                onQueryChange = onValueChange,
                onSearch = onSearch,
                onExpandedChange = onExpandChange,
                placeholder = {
                    Text(
                        text = "Search",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.secondary,
                ),
                trailingIcon = {
                    UserImage(
                        photoUrl = currentUser.imageUrl,
                        userName = currentUser.name,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                goToProfile()
                            }
                    )
                },
                leadingIcon = {
                    IconButton(
                        onClick = {
                            onExpandChange(!expand)
                        }
                    ) {
                        Icon(
                            imageVector = if (!expand) Icons.Rounded.Search else Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                expanded = expand,
            )
        },
        expanded = expand,
        onExpandedChange = onExpandChange,
        shape = RoundedCornerShape(32.dp),
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            dividerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        ScreenStateHost(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            success = { users ->
                SearchResultContent(
                    userList = users,
                    modifier = Modifier.fillMaxWidth(),
                    onUserClick = { account ->
                        onAvatarClick(account)
                    }
                )
            },
            failure = {
                Icon(
                    imageVector = Icons.Rounded.WifiOff,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            },
            loading = {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}


@Composable
fun SearchResultContent(
    userList: List<Account>,
    onUserClick: (account: Account) -> Unit,
    modifier: Modifier = Modifier
){
    if (userList.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.SearchOff,
                modifier = Modifier.size(56.dp),
                contentDescription = null,
            )

            Text(
                text = stringResource(R.string.conversation_screen_search_empty),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyRow(
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(
                items = userList,
                key = { user -> user.id}
            ){ item ->
                UserItem(
                    userName = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .clickable {
                            onUserClick(item)
                        }

                )
            }
        }
    }
}

@Composable
fun UserItem(
    userName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserImage(userName = userName)
        Spacer(Modifier.height(8.dp))
        Text(
            text = userName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

