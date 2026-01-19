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
import androidx.compose.material.icons.rounded.MoreVert
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
import com.nhuhuy.replee.feature_chat.presentation.setting.component.MainOptionRow
import com.nhuhuy.replee.feature_chat.presentation.setting.component.InformationUser
import com.nhuhuy.replee.feature_chat.presentation.setting.component.SecondaryOption
import com.nhuhuy.replee.feature_chat.presentation.setting.component.SecondaryOptionItem
import com.nhuhuy.replee.feature_chat.presentation.setting.state.OptionAction
import com.nhuhuy.replee.feature_chat.presentation.setting.state.OptionState

@Composable
fun OptionScreen(
    state: OptionState,
    onAction: (OptionAction) -> Unit
) {
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
            modifier = Modifier.fillMaxSize()
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
                MainOptionRow(
                    onOptionSelect = { option ->
                        onAction(OptionAction.OnMainOptionSelect(option))
                    }
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            val secondaryOptionList = SecondaryOption.entries.toList()

            items(
                count = secondaryOptionList.size,
                key = { index -> index}
            ){ index ->
                val firstItem = index == 0
                val lastItem = index == secondaryOptionList.lastIndex
                SecondaryOptionItem(
                    res = secondaryOptionList[index].label,
                    icon = secondaryOptionList[index].icon,
                    sub = secondaryOptionList[index].content,
                    shape = if (firstItem){
                        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp,
                            bottomEnd = 4.dp, bottomStart = 4.dp)
                    } else if (lastItem){
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp,
                            topStart = 4.dp, topEnd = 4.dp)
                    } else {
                        RoundedCornerShape(8.dp)
                    },
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable{
                        //TODO(")
                    }
                )

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationTopBar(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
){
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

