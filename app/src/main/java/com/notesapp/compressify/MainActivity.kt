package com.notesapp.compressify

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.notesapp.compressify.domain.model.Event
import com.notesapp.compressify.domain.model.NavigationRoutes
import com.notesapp.compressify.ui.components.home.common.CompressionCompletedDialog
import com.notesapp.compressify.ui.components.home.HomeScreen
import com.notesapp.compressify.ui.components.image.CompressOptionsScreen
import com.notesapp.compressify.ui.components.video.SelectVideoScreen
import com.notesapp.compressify.ui.theme.CompressifyTheme
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import com.notesapp.compressify.util.UIEvent
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
            var showCompressionCompletedDialog by remember {
                mutableStateOf(false)
            }
            LaunchedEffect(key1 = Unit) {
                viewModel.eventsFlow.collect { event ->
                    when (event) {
                        is Event.CompressionCompleted -> {
                            showCompressionCompletedDialog = true
                        }

                        Event.Compressing -> {}
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
                onResult = { viewModel.onImageSelected(it) }
            )
            selectedVideoLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickMultipleVisualMedia(),
                onResult = { viewModel.onVideoSelected(it) }
            )
            CompressifyTheme {
                val selectedImages by viewModel.selectedImages.collectAsState()
                val categories by viewModel.categoryStorage.collectAsState()
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
                            CompressOptionsScreen(
                                selectedImages = selectedImages,
                                onUIEvent = viewModel::onUIEvent,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        composable(NavigationRoutes.COMPRESS_VIDEO.name) {

                        }

                    }
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

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        destination.route?.let {
            viewModel.onUIEvent(UIEvent.Navigate(NavigationRoutes.valueOf(it)))
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


