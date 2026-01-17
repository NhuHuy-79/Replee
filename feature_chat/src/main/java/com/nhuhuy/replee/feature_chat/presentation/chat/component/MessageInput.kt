package com.nhuhuy.replee.feature_chat.presentation.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.feature_chat.R

@Composable
fun MessageInput(
    value: String,
    onValueChange: (value: String) -> Unit,
    onFocusChange: (focus: Boolean) -> Unit,
    onCameraClick:() -> Unit,
    onImageClick: () -> Unit,
    onSendMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.CameraAlt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
                .clickable(onClick = onCameraClick)
        )

        Icon(
            imageVector = Icons.Rounded.Image,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
                .clickable(onClick = onImageClick)
        )

        Spacer(Modifier.width(8.dp))

        MessageTextField(
            value = value,
            onValueChange = onValueChange,
            onFocusChange = onFocusChange,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onSendMessage,
            enabled = value.isNotBlank(),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = if (value.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Send,
                contentDescription = null,
            )
        }
    }
}

@Composable
fun MessageTextField(
    value: String,
    onValueChange: (value: String) -> Unit,
    onFocusChange: (value: Boolean) -> Unit,
    modifier: Modifier = Modifier){
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(32.dp)
        ).padding(horizontal = 16.dp, vertical = 14.dp)
            .onFocusChanged{ focusState ->
                onFocusChange(focusState.isFocused)
            }
        ,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary
        ),
        decorationBox = { innerTextField ->
           Box(
               modifier = Modifier.fillMaxWidth()
           ){
               if (value.isBlank()) {
                   Text(
                       text = stringResource(R.string.chat_screen_placeholder),
                       style = MaterialTheme.typography.bodyLarge,
                       color = MaterialTheme.colorScheme.onSurfaceVariant
                   )
               }

               innerTextField()
           }
        }
    )
}

@Preview
@Composable
private fun InputPreview() {

}