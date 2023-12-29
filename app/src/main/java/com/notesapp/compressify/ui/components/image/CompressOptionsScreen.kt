package com.notesapp.compressify.ui.components.image

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.ui.theme.primaryGreen
import com.notesapp.compressify.util.UIEvent

@Composable
fun CompressOptionsScreen(
    modifier: Modifier = Modifier,
    selectedImages: List<ImageModel>,
    onUIEvent : (UIEvent) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f)) {
            items(selectedImages.size) {
                ImagePreviewCard(
                    image = selectedImages[it],
                    modifier = Modifier.padding(8.dp),
                    onDeleteClick = {
                        onUIEvent(UIEvent.Images.RemoveImageClicked(it))
                    }
                )
            }
        }
        Button(
            onClick = { showDialog = true }, modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Continue")
        }
        if (showDialog) {
            OpenCompressDialog(modifier = Modifier.fillMaxWidth(), onDismiss = {
                showDialog = false
            },
                onConfirm = { resolution, quality, keepOriginal ->
                    showDialog = false
                    onUIEvent(UIEvent.Images.CompressionOptionsConfirmed(resolution, quality, keepOriginal))
                },
            )
        }

    }
}

@Composable
fun OpenCompressDialog(
    modifier: Modifier,
    onDismiss: (Boolean) -> Unit,
    onConfirm: (Float, Float, Boolean) -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss(false) }) {
        var resolution by remember {
            mutableFloatStateOf(0.9f)
        }
        var quality by remember {
            mutableFloatStateOf(0.9f)
        }
        var keepOriginal by remember {
            mutableStateOf(true)
        }
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
                        text = "Compress Options",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Select Resolution",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Slider(
                            value = resolution,
                            valueRange = 0.1f..1f,
                            colors = SliderDefaults.colors(
                                activeTrackColor = primaryGreen,
                                thumbColor = primaryGreen,
                                inactiveTrackColor = primaryGreen.copy(alpha = 0.7f)
                            ),
                            onValueChange = {
                                resolution = it
                            })
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Select Quality",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Slider(
                            value = quality,
                            valueRange = 0.1f..1f,
                            colors = SliderDefaults.colors(
                                activeTrackColor = primaryGreen,
                                thumbColor = primaryGreen,
                                inactiveTrackColor = primaryGreen.copy(alpha = 0.7f)
                            ),
                            onValueChange = {
                                quality = it
                            })
                    }
                }
                Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = keepOriginal,
                        onCheckedChange = { keepOriginal = it},
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Text(
                        text = "Keep Original",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Button(
                    onClick = {
                        onConfirm(resolution, quality, keepOriginal)
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Continue")
                }
            }
        }
    }

}
