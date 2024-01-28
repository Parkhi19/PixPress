package com.notesapp.compressify.ui.components.home.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.ui.theme.primaryColor

@Composable
fun GrantStoragePermission(
    modifier: Modifier = Modifier,
    onGrantPermissionClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "OOPS!", style = MaterialTheme.typography.headlineMedium, color = primaryColor)
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "We need storage permission to compress your files",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.size(16.dp))
        PrimaryButton(buttonText = "Grant Permission", onClick = onGrantPermissionClick)

    }
}