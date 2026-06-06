package com.nhuhuy.replee.feature_chat.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.presentation.component.BoxContainer
import com.nhuhuy.replee.core.presentation.component.CircularLoadingContent
import com.nhuhuy.replee.feature_chat.presentation.search.component.SearchResultItem
import com.nhuhuy.replee.feature_chat.presentation.search.component.SearchTopBar
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchAction
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchState

@Composable
fun SearchScreen(
    state: SearchState,
    searchResults: LazyPagingItems<Message>,
    onAction: (SearchAction) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SearchTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                query = state.searchQuery,
                onQueryChange = { query: String ->
                    onAction(SearchAction.OnQueryChange(query))
                },
                onSearch = {
                    focusManager.clearFocus()
                    onAction(SearchAction.OnSearch)
                },
                onClose = {
                    focusManager.clearFocus()
                    onAction(SearchAction.OnSearchClose)
                },
                onBackClick = {
                    keyboard?.hide()
                    onAction(SearchAction.OnNavigateBack)
                }
            )
        }
    ) { innerPadding ->
        BoxContainer(
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(
                    count = searchResults.itemCount,
                    key = searchResults.itemKey { message -> message.messageId },
                    contentType = searchResults.itemContentType { message -> message.type }
                ) { index ->
                    val message = searchResults[index]
                    message?.let {
                        val sender =
                            if (message.senderId == state.currentUser.id) state.currentUser else state.otherUser
                        SearchResultItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable {
                                    onAction(SearchAction.OnMessagePress(message))
                                }
                                .animateItem(),
                            message = message,
                            senderName = sender.name,
                            senderImgUrl = sender.imageUrl
                        )
                    }
                }
            }

            if (searchResults.itemCount == 0) {
                Icon(
                    imageVector = Icons.Rounded.SearchOff,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
            }

            if (searchResults.loadState.refresh is LoadState.Loading) {
                CircularLoadingContent()
            }
        }
    }
}
