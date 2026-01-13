package com.nhuhuy.replee.feature_chat.presentation.information

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.feature_chat.presentation.information.component.InformationOptionRow
import com.nhuhuy.replee.feature_chat.presentation.information.component.InformationUser
import com.nhuhuy.replee.feature_chat.presentation.information.state.InformationAction
import com.nhuhuy.replee.feature_chat.presentation.information.state.InformationState

@Composable
fun InformationScreen(
    state: InformationState,
    onAction: (InformationAction) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            InformationTopBar(
                onBackPressed = {
                    onAction(InformationAction.OnBackPressed)
                },
                onMoreClick = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InformationUser(
                userName = state.otherUserName,
                email = state.otherUserEmail,
                modifier = Modifier
            )
            InformationOptionRow(
                onOptionSelect = { option ->
                    onAction(InformationAction.OnOptionSelect(option))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationTopBar(
    onBackPressed: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
){
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onBackPressed
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null
                )
            }
        },
        title = {},
        actions = {
            IconButton(
                onClick = onMoreClick
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }
        },
    )
}

