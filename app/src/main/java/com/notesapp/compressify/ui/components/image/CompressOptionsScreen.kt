package com.notesapp.compressify.ui.components.image

import android.util.Log
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notesapp.compressify.R
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.ui.components.home.common.PrimaryButton
import com.notesapp.compressify.ui.components.home.common.PrimaryImageButton
import com.notesapp.compressify.ui.theme.primaryColor
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import com.notesapp.compressify.util.UIEvent
import com.notesapp.compressify.util.getFormattedSize

@Composable
fun CompressOptionsScreen(
    modifier: Modifier = Modifier,
    selectedImages: List<ImageModel>,
    onUIEvent: (UIEvent) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    val selectedPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { onUIEvent(UIEvent.Images.OnImagesAdded(it)) }
    )

    Column(modifier = modifier) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp,
                pressedElevation = 2.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val totalSelectedImages = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("${selectedImages.size} ")
                        }
                        append("Selected ")
                    }

                    Text(
                        text = totalSelectedImages,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    val totalSelectedImagesSize =
                        selectedImages.sumOf { it.size }.getFormattedSize()

                    Text(
                        text = totalSelectedImagesSize,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )

                }
                PrimaryImageButton(
                    icon = R.drawable.ic_upload,
                    onClick = {
                        selectedPhotoLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    buttonText = "Add More"
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
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
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_resolution),
                contentDescription = "",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            PrimaryButton(
                onClick = { /*TODO*/ },
                buttonText = "Continue",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (showDialog) {
            OpenCompressDialog(
                modifier = Modifier.fillMaxWidth(),
                onDismiss = {
                    showDialog = false
                },
                onConfirm = { resolution, quality, keepOriginal ->
                    showDialog = false
                    onUIEvent(
                        UIEvent.Images.CompressionOptionsConfirmed(
                            resolution,
                            quality,
                            keepOriginal
                        )
                    )
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
                                activeTrackColor = primaryColor,
                                thumbColor = primaryColor,
                                inactiveTrackColor = primaryColor.copy(alpha = 0.7f)
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
                                activeTrackColor = primaryColor,
                                thumbColor = primaryColor,
                                inactiveTrackColor = primaryColor.copy(alpha = 0.7f)
                            ),
                            onValueChange = {
                                quality = it
                            })
                    }
                }
                Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = keepOriginal,
                        onCheckedChange = { keepOriginal = it },
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

