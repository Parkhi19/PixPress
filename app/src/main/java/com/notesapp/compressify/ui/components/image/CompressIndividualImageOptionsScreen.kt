package com.notesapp.compressify.ui.components.image

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.ui.components.home.common.CompressOptionsHeader
import com.notesapp.compressify.ui.components.home.common.PrimaryButton
import com.notesapp.compressify.util.UIEvent
import com.notesapp.compressify.util.getAbsoluteImagePath

@Composable
fun CompressIndividualImageOptionsScreen(
    modifier: Modifier = Modifier,
    selectedImages: List<ImageModel>,
    onUIEvent: (UIEvent) -> Unit
) {

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
                item {
                    IndividualImageCompressedCard(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillParentMaxWidth(0.9f),
                        image = image,
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

