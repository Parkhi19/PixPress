package com.notesapp.compressify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.activity.result.ActivityResultLauncher
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
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.notesapp.compressify.domain.model.Event
import com.notesapp.compressify.domain.model.NavigationRoutes
import com.notesapp.compressify.domain.useCase.CompressAndSaveImagesUseCase
import com.notesapp.compressify.domain.useCase.CompressAndSaveVideoUseCase
import com.notesapp.compressify.domain.useCase.GetLibraryItemsUseCase
import com.notesapp.compressify.ui.components.home.HomeScreen
import com.notesapp.compressify.ui.components.home.common.GrantStoragePermission
import com.notesapp.compressify.ui.components.image.CompressImageOptionsScreen
import com.notesapp.compressify.ui.components.image.CompressIndividualImageOptionsScreen
import com.notesapp.compressify.ui.components.video.CompressIndividualVideoScreen
import com.notesapp.compressify.ui.components.video.CompressVideoOptionsScreen
import com.notesapp.compressify.ui.library.LibraryScreen
import com.notesapp.compressify.ui.theme.CompressifyTheme
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MainActivity : ComponentActivity(), NavController.OnDestinationChangedListener {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var selectedPhotoLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<Uri>>
    private lateinit var selectedVideoLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<Uri>>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private val storagePermissionGrantedFlow = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            storagePermissionGrantedFlow.value = if (isGranted) {
                true
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Storage Permission is required to use this app",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
        }
        setContent {
            val navController = rememberNavController()
            val storagePermissionsGranted by storagePermissionGrantedFlow.collectAsState()

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
                onResult = { uris ->
                    if (uris.isNotEmpty()) {
                        viewModel.onVideoSelected(
                            uris = uris
                        )
                    }

                }
            )
            checkForStoragePermissions()
            CompressifyTheme {
                val selectedImages by viewModel.selectedImages.collectAsState()
                val categories by viewModel.categoryStorage.collectAsState()

                val selectedVideos by viewModel.selectedVideos.collectAsState()
                val isVideoProcessing by viewModel.selectedVideosProcessing.collectAsState()

                val compressImagesUIState by viewModel.compressImagesUIState.collectAsState()
                val compressVideosUIState by viewModel.compressVideosUIState.collectAsState()

                val imageCompressionOptions by viewModel.allImageCompressOptions.collectAsState()
                val videoCompressionOptions by viewModel.allVideoCompressOptions.collectAsState()

                val notDeletedImages by viewModel.notDeletedImages.collectAsState()


                if (storagePermissionsGranted) {
                    checkForNotificationPermissions()
                }

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (storagePermissionsGranted) {
                        LaunchedEffect(key1 = Unit) {
                            navController.addOnDestinationChangedListener(this@MainActivity)
                            viewModel.currentRoute.collectLatest { route ->
                                navController.navigate(route.name)
                            }
                        }
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
                                    onLibraryButtonClick = {

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
                                    compressVideosUIState = compressVideosUIState,
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

                            composable(NavigationRoutes.INDIVIDUAL_VIDEO_PREVIEW.name) {
                                CompressIndividualVideoScreen(
                                    selectedVideos = selectedVideos,
                                    modifier = Modifier.fillMaxSize(),
                                    compressionOptions = videoCompressionOptions,
                                    onUIEvent = viewModel::onUIEvent
                                )
                            }
                            composable(NavigationRoutes.LIBRARY.name) {
                                LibraryScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    initialAllItemsSelected = false,
                                    notDeletedImages = notDeletedImages
                                )
                            }

                        }
                    } else {
                        GrantStoragePermission(
                            modifier = Modifier.fillMaxSize(),
                            onGrantPermissionClick = {
                                openStoragePermissionsPage()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun openStoragePermissionsPage() {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val getStoragePermission = Intent()
                getStoragePermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getStoragePermission)
            }
            return
        }
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.setData(uri)
        startActivity(intent)
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
        storagePermissionGrantedFlow.value = checkForStoragePermissions(false)
        lifecycleScope.launch {
            storagePermissionGrantedFlow.collectLatest {
                if (it) {
                    viewModel.syncStorageCategory()
                }
            }
        }
    }

    private fun checkForStoragePermissions(launchPermissionFlow: Boolean = true): Boolean {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                if (!launchPermissionFlow) {
                    return false
                }
                val getStoragePermission = Intent()
                getStoragePermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getStoragePermission)
                return false
            }
            return true
        }
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (!launchPermissionFlow) {
                return false
            }
            requestPermissionLauncher.launch(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            return false
        }
        return true
    }

    private fun checkForNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(intent)
            }
        }
    }
}


