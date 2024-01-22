package com.notesapp.compressify.ui.components.home.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.R
import com.notesapp.compressify.domain.model.NavigationRoutes
import com.notesapp.compressify.util.UIEvent

@Composable
fun BottomBar(modifier: Modifier = Modifier, onUIEvent: (UIEvent) -> Unit) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier.shadow(4.dp),
        actions = {
            MenuItem(icon = R.drawable.compress_photo, text = "Photo") {
                onUIEvent(UIEvent.Navigate(NavigationRoutes.COMPRESS_IMAGE))
            }
            MenuItem(icon = R.drawable.compress_video, text = "Video") {
                onUIEvent(UIEvent.Navigate(NavigationRoutes.COMPRESS_VIDEO))
            }
        }

    )
}

@Composable
fun RowScope.MenuItem(@DrawableRes icon: Int, text: String, onClick: () -> Unit) {
    Column(modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
        .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = "",
            modifier = Modifier.size(36.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(text = text, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary)
    }
}