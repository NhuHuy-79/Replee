package com.nhuhuy.replee.feature_chat.presentation.information.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Message
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.feature_chat.R

@Composable
fun InformationOptionRow(
    modifier: Modifier = Modifier,
    onOptionSelect: (option: Option) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Option.entries.forEach { option ->
            InformationOption(
                option = option,
                onClick = { onOptionSelect(option) }
            )
        }
    }
}

@Composable
fun InformationOption(
    option: Option,
    modifier: Modifier = Modifier,
    onClick: (option: Option) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = {
                onClick(option)
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.padding(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null
            )
        }

        Text(
            text = stringResource(option.label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

enum class Option(@StringRes val label: Int, val icon: ImageVector){
    MESSAGE(R.string.information_screen_message, Icons.AutoMirrored.Rounded.Message),
    CALL(R.string.information_screen_call, Icons.Rounded.Call),
    MUTE(R.string.information_screen_mute, Icons.Rounded.NotificationsOff)
}