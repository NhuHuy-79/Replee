package com.nhuhuy.replee.feature_chat.presentation.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.presentation.search.component.SearchResultItem
import com.nhuhuy.replee.feature_chat.presentation.search.component.SearchTopBar
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchAction
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchState

@Composable
fun SearchScreen(
    query: String,
    state: SearchState,
    searchResults: List<Message>,
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
                query = query,
                onQueryChange = { query: String ->
                    onAction(SearchAction.OnQueryChange(query))
                },
                onSearch = {},
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(
                items = searchResults,
                key = { message -> message.messageId }
            ) { message ->
                val sender =
                    if (message.senderId == state.currentUser.id) state.currentUser else state.otherUser
                SearchResultItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .animateItem(),
                    message = message,
                    senderName = sender.name,
                    senderImgUrl = sender.imageUrl
                )
            }
        }

    }
}