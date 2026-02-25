package com.nhuhuy.replee.feature_profile.presentation.profile.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.AuthServiceProvider
import com.nhuhuy.replee.core.design_system.component.UserImage

@Composable
fun ProfileUserCard(
    user: Account,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.border(
                width = 2.dp,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            UserImage(
                photoUrl = user.imageUrl,
                userName = user.name,
            )
        }


        Column(
            modifier = Modifier
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        FilledTonalIconButton(
            enabled = user.provider == AuthServiceProvider.EMAIL,
            onClick = onEditClick,
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = null
            )
        }
    }
}