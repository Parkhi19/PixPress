package com.notesapp.compressify

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.domain.model.Event
import com.notesapp.compressify.ui.components.common.CompressionCompletedDialog
import com.notesapp.compressify.ui.components.image.CompressImageScreen
import com.notesapp.compressify.ui.theme.CompressifyTheme
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var selectedPhotoLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<Uri>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var showCompressionCompletedDialog by remember {
                mutableStateOf(false)
            }
            LaunchedEffect(key1 = Unit) {
                viewModel.eventsFlow.collect { event ->
                    when (event) {
                        is Event.CompressionCompleted -> {
                            showCompressionCompletedDialog = true
                        }
                    }
                }
            }
            selectedPhotoLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickMultipleVisualMedia(),
                onResult = { viewModel.onImageSelected(it) }
            )
            CompressifyTheme {
                val selectedImages by viewModel.selectedImages.collectAsState()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompressImageScreen(
                        modifier = Modifier.fillMaxSize(),
                        selectedImages = selectedImages,
                        onImageSelectClick = {
                            selectedPhotoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onImageDelete = { viewModel.onImageDelete(it) },
                        onConfirm = { resolution, quality, keepOriginal ->
                            viewModel.onConfirm(resolution, quality, keepOriginal)
                        }
                    )
                    if (showCompressionCompletedDialog) {
                        CompressionCompletedDialog(
                            modifier = Modifier
                                .height(300.dp)
                                .width(300.dp),
                        ) {
                            showCompressionCompletedDialog = false
                        }
                    }
                }
            }
        }

    }


}


