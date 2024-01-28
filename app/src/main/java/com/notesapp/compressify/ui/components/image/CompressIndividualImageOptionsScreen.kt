package com.notesapp.compressify.ui.components.image

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.ui.components.home.common.CompressOptionsHeader
import com.notesapp.compressify.ui.components.home.common.PrimaryButton
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import com.notesapp.compressify.util.UIEvent
import com.notesapp.compressify.util.getAbsoluteImagePath

@Composable
fun CompressIndividualImageOptionsScreen(
    modifier: Modifier = Modifier,
    selectedImages: List<ImageModel>,
    compressionOptions: MainViewModel.ImageCompressionOptions,
    onUIEvent: (UIEvent) -> Unit
) {
    val individualOptions = remember(compressionOptions) {
        mutableStateMapOf<Uri, MainViewModel.ImageCompressionOptions>().apply {
            putAll(selectedImages.map { it.uri to compressionOptions })
        }
    }
    val selectedPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            onUIEvent(UIEvent.Images.OnImagesAdded(uris))
        }
    )

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
        LazyRow(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (image in selectedImages) {
                val options by derivedStateOf {
                    individualOptions[image.uri] ?: compressionOptions
                }
                item {
                    IndividualImageCompressedCard(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillParentMaxWidth(0.9f),
                        image = image,
                        compressionOptions = options,
                        onCompressionOptionsChanged = {
                            individualOptions[image.uri] = it
                        },
                        onUIEvent = onUIEvent
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (selectedImages.isNotEmpty()) {
            PrimaryButton(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                onClick = { /*TODO*/ },
                buttonText = "Start Compression"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

