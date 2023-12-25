package com.notesapp.compressify.ui.components.image

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.notesapp.compressify.domain.model.ImageModel

@Composable
fun ImagePreviewCard(modifier: Modifier = Modifier, image:ImageModel) {
    Card(modifier = modifier, shape = MaterialTheme.shapes.medium) {
        Column {
            Image(bitmap = image.thumbnail.asImageBitmap(), contentDescription = "", modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column{
                    Text(text = image.name, style = MaterialTheme.typography.bodyMedium)
                    Text(text = image.size.toString(), style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "")
                }
            }
        }
    }

    }
