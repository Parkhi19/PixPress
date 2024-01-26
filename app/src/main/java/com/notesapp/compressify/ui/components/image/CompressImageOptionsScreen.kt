package com.notesapp.compressify.ui.components.image

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.ui.components.home.common.CompressOptionsFooter
import com.notesapp.compressify.ui.components.home.common.CompressOptionsHeader
import com.notesapp.compressify.ui.components.home.common.PrimaryButton
import com.notesapp.compressify.ui.theme.primaryColor
import com.notesapp.compressify.ui.theme.primaryTintedColor
import com.notesapp.compressify.util.UIEvent
import kotlinx.coroutines.launch

@Composable
fun CompressImageOptionsScreen(
    modifier: Modifier = Modifier,
    selectedImages: List<ImageModel>,
    isImageProcessing: Boolean,
    onUIEvent: (UIEvent) -> Unit
) {
    var showOptionsBottomSheet by remember {
        mutableStateOf(false)
    }

    val selectedPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { onUIEvent(UIEvent.Images.OnImagesAdded(it)) }
    )
    if (isImageProcessing) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp), color = primaryColor)
        }
    }
    Column(modifier = modifier) {
        CompressOptionsHeader(
            modifier = Modifier.fillMaxWidth(),
            numberOfFiles = selectedImages.size,
            filesSize = selectedImages.sumOf { it.size }
        ) {
            selectedPhotoLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
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

        if (selectedImages.isNotEmpty()) {
            CompressOptionsFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                onOptionsClick = {
                    showOptionsBottomSheet = true
                },
                onContinueClick = {

                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        if (showOptionsBottomSheet) {
            OpenCompressDialog(
                modifier = Modifier.fillMaxWidth(),
                onDismiss = {
                    showOptionsBottomSheet = false
                },
                onConfirm = { resolution, quality, keepOriginal ->
                    onUIEvent(
                        UIEvent.Images.ImageCompressionOptionsConfirmed(
                            resolution,
                            quality,
                            keepOriginal
                        )
                    )
                }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenCompressDialog(
    modifier: Modifier,
    onDismiss: () -> Unit,
    onConfirm: (Float, Float, Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = { onDismiss() }, sheetState = sheetState) {
        val textColor = MaterialTheme.colorScheme.onBackground
        var resolution by remember {
            mutableFloatStateOf(0.9f)
        }
        var quality by remember {
            mutableFloatStateOf(0.9f)
        }
        var deleteOriginal by remember {
            mutableStateOf(false)
        }
        Card(
            shape = RoundedCornerShape(
                size = 8.dp
            ),
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Compress Options",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = textColor
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(primaryTintedColor)
                        .padding(vertical = 12.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "Select Resolution",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier,
                                color = textColor
                            )
                            Text(
                                text = "${(resolution * 100).toInt()} %",
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor
                            )
                        }
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "Select Quality",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp),
                                color = textColor
                            )
                            Text(
                                text = "${(quality * 100).toInt()} %",
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor
                            )
                        }
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
                        checked = deleteOriginal,
                        onCheckedChange = { deleteOriginal = it },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Text(
                        text = "Delete Original",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp),
                        color = textColor
                    )
                }
                PrimaryButton(
                    onClick = { onConfirm(resolution, quality, deleteOriginal)
                               coroutineScope.launch {
                                   sheetState.hide()
                               }.invokeOnCompletion {
                                   if (!sheetState.isVisible) {
                                       onDismiss()
                                   }
                               }
                              },
                    buttonText = "Apply",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }
        }
    }
}

