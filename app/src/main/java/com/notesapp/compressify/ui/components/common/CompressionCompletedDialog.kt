package com.notesapp.compressify.ui.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.notesapp.compressify.R
import com.notesapp.compressify.ui.theme.primaryGreen

@Composable
fun CompressionCompletedDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(
                size = 8.dp
            ), modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Compression Completed",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                Icon(
                    painter = painterResource(id = R.drawable.done),
                    contentDescription = "Close",
                    tint = primaryGreen,
                    modifier = Modifier
                        .size(128.dp)
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}



