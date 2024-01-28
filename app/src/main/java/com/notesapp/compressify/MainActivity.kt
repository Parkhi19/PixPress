package com.notesapp.compressify

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.notesapp.compressify.domain.model.Event
import com.notesapp.compressify.domain.model.NavigationRoutes
import com.notesapp.compressify.ui.components.home.HomeScreen
import com.notesapp.compressify.ui.components.image.CompressImageOptionsScreen
import com.notesapp.compressify.ui.components.image.CompressIndividualImageOptionsScreen
import com.notesapp.compressify.ui.components.video.CompressVideoOptionsScreen
import com.notesapp.compressify.ui.theme.CompressifyTheme
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MainActivity : ComponentActivity(), NavController.OnDestinationChangedListener {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var selectedPhotoLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<Uri>>
    private lateinit var selectedVideoLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<Uri>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForStoragePermissions()
        setContent {
            val navController = rememberNavController()

            LaunchedEffect(key1 = Unit) {
                viewModel.eventsFlow.collect { event ->
                    when (event) {
                        is Event.PopBackStackTo -> {
                            navController.popBackStack(event.destination.name, false)
                        }

                        is Event.ShowToast -> {
                            Toast.makeText(this@MainActivity, event.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            LaunchedEffect(key1 = Unit) {
                navController.addOnDestinationChangedListener(this@MainActivity)
                viewModel.currentRoute.collectLatest { route ->
                    navController.navigate(route.name)
                }
            }
            selectedPhotoLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickMultipleVisualMedia(),
                onResult = { uris ->
                    if (uris.isNotEmpty()) {
                        viewModel.onImageSelected(
                            uris = uris
                        )
                    }
                }
            )
            selectedVideoLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickMultipleVisualMedia(),
                onResult = {uris->
                    if(uris.isNotEmpty()){
                        viewModel.onVideoSelected(
                           uris = uris
                        )
                    }

                }
            )
            CompressifyTheme {
                val selectedImages by viewModel.selectedImages.collectAsState()
                val categories by viewModel.categoryStorage.collectAsState()
                val isImageProcessing by viewModel.selectedImagesProcessing.collectAsState()

                val selectedVideos by viewModel.selectedVideos.collectAsState()
                val isVideoProcessing by viewModel.selectedVideosProcessing.collectAsState()

                val compressImagesUIState by viewModel.compressImagesUIState.collectAsState()

                val imageCompressionOptions by viewModel.allImageCompressOptions.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = NavigationRoutes.HOME.name,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(NavigationRoutes.HOME.name) {
                            HomeScreen(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(state = rememberScrollState()),
                                categories = categories,
                                onCompressImageClick = {
                                    selectedPhotoLauncher.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                },
                                onCompressVideoClick = {
                                    selectedVideoLauncher.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.VideoOnly
                                        )
                                    )
                                },
                                onUIEvent = viewModel::onUIEvent
                            )
                        }

                        composable(NavigationRoutes.COMPRESS_IMAGE.name) {
                            CompressImageOptionsScreen(
                                compressImagesUIState = compressImagesUIState,
                                onUIEvent = viewModel::onUIEvent,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        composable(NavigationRoutes.COMPRESS_VIDEO.name) {
                            CompressVideoOptionsScreen(
                                selectedVideos = selectedVideos,
                                isVideoProcessing = isVideoProcessing,
                                modifier = Modifier.fillMaxSize(),
                                onUIEvent = viewModel::onUIEvent
                            )
                        }

                        composable(NavigationRoutes.INDIVIDUAL_IMAGE_PREVIEW.name) {
                            CompressIndividualImageOptionsScreen(
                                selectedImages = selectedImages,
                                modifier = Modifier.fillMaxSize(),
                                compressionOptions = imageCompressionOptions,
                                onUIEvent = viewModel::onUIEvent
                            )
                        }

                    }
                }
            }
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        destination.route?.let {
            controller.popBackStack(it, false)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.syncStorageCategory()
    }

    private fun checkForStoragePermissions() {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val getStoragePermission = Intent()
                getStoragePermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getStoragePermission)
            }
        }
    }
}


