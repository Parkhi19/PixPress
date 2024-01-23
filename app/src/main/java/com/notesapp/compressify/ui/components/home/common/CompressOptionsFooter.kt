package com.notesapp.compressify.ui.components.home.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.R

@Composable
fun CompressOptionsFooter(
    modifier: Modifier,
    onOptionsClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_resolution),
            contentDescription = "",
            modifier = Modifier
                .size(48.dp)
                .clickable {
                    onOptionsClick()
                }
        )
        Spacer(modifier = Modifier.width(24.dp))
        PrimaryButton(
            onClick = { onContinueClick() },
            buttonText = "Continue",
            modifier = Modifier.weight(1f)
        )
    }
}