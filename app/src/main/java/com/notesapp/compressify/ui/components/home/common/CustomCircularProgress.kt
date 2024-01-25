package com.notesapp.compressify.ui.components.home.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.ui.theme.primaryColor
import com.notesapp.compressify.ui.theme.primaryTintedColor
import com.notesapp.compressify.util.precised

@Composable
fun CustomCircularProgress(
    modifier: Modifier = Modifier,
    progress: Float,
    showText: Boolean = true
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            color = primaryColor,
            strokeWidth = 8.dp,
            progress = { progress / 100 },
            trackColor = primaryTintedColor,
            strokeCap = StrokeCap.Round
        )
        if (showText) {
            Text(
                text = "${progress.precised(1)}%",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = primaryColor
            )
        }
    }
}

@Preview
@Composable
fun PreviewCircularProgress() {
    CustomCircularProgress(progress = 48.3f, modifier = Modifier.size(64.dp))
}