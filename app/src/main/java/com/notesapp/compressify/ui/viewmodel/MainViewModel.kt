package com.notesapp.compressify.ui.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesapp.compressify.CompressApplication
import com.notesapp.compressify.domain.model.CategoryModel
import com.notesapp.compressify.domain.model.Event
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.domain.model.LibraryModel
import com.notesapp.compressify.domain.model.NavigationRoutes
import com.notesapp.compressify.domain.model.VideoModel
import com.notesapp.compressify.domain.useCase.AddLibraryItemUseCase
import com.notesapp.compressify.domain.useCase.BaseUseCase
import com.notesapp.compressify.domain.useCase.CompressAndSaveImagesUseCase
import com.notesapp.compressify.domain.useCase.CompressAndSaveVideoUseCase
import com.notesapp.compressify.domain.useCase.DeleteOriginalUseCase
import com.notesapp.compressify.domain.useCase.GetCategoryStorageUseCase
import com.notesapp.compressify.ui.components.image.CompressImagesUIState
import com.notesapp.compressify.util.UIEvent
import com.notesapp.compressify.util.getAbsoluteImagePath
import com.notesapp.compressify.util.getAbsoluteVideoPath
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val compressAndSaveImagesUseCase: CompressAndSaveImagesUseCase,
    private val compressAndSaveVideosUseCase: CompressAndSaveVideoUseCase,
    private val getCategoryStorageUseCase: GetCategoryStorageUseCase,
    private val addLibraryItemUseCase: AddLibraryItemUseCase,
    private val deleteOriginalUseCase: DeleteOriginalUseCase
) : ViewModel() {

    private val _categoryStorage = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categoryStorage = _categoryStorage.asStateFlow()

    private val _selectedImages = MutableStateFlow<List<ImageModel>>(emptyList())
    val selectedImages = _selectedImages.asStateFlow()

    private val _selectedVideos = MutableStateFlow<List<VideoModel>>(emptyList())
    val selectedVideos = _selectedVideos.asStateFlow()

    private val _selectedImagesProcessing = MutableStateFlow(false)
    val selectedImagesProcessing = _selectedImagesProcessing.asStateFlow()

    private val _selectedVideosProcessing = MutableStateFlow(false)
    val selectedVideosProcessing = _selectedVideosProcessing.asStateFlow()

    private val _currentRoute = MutableStateFlow(NavigationRoutes.HOME)
    val currentRoute = _currentRoute.asStateFlow()

    private val _allImageCompressOptions = MutableStateFlow(ImageCompressionOptions())
    val allImageCompressOptions = _allImageCompressOptions.asStateFlow()


    val compressImagesUIState = combine(
        selectedImages,
        selectedImagesProcessing
    ) { images, isProcessing ->
        CompressImagesUIState(
            selectedImages = images,
            isImageProcessing = isProcessing
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CompressImagesUIState())

    private val eventChannel = Channel<Event> { }
    val eventsFlow = eventChannel.receiveAsFlow()


    fun onImageSelected(uris: List<Uri>) {
        onUIEvent(UIEvent.Navigate(NavigationRoutes.COMPRESS_IMAGE))
        viewModelScope.launch(Dispatchers.IO) {
            _selectedImagesProcessing.value = true
            _selectedImages.value = uris.mapNotNull {
                it.getAbsoluteImagePath()
            }.map {
                async {
                    ImageModel(uri = it)
                }
            }.awaitAll()
            _selectedImagesProcessing.value = false
        }

    }

    fun onVideoSelected(uris: List<Uri>) {
        onUIEvent(UIEvent.Navigate(NavigationRoutes.COMPRESS_VIDEO))
        viewModelScope.launch(Dispatchers.IO) {
            _selectedVideosProcessing.value = true
            _selectedVideos.value = uris.mapNotNull {
                it.getAbsoluteVideoPath()
            }.map {
                async {
                    VideoModel(uri = it)
                }
            }.awaitAll()
            _selectedVideosProcessing.value = false
        }
    }

    private fun onImageAdded(uris: List<Uri>) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedImagesProcessing.value = true
            _selectedImages.value = _selectedImages.value + uris.mapNotNull {
                it.getAbsoluteImagePath()
            }.filter { uri ->
                _selectedImages.value.none { it.uri.path == uri.path }
            }.map {
                async {
                    ImageModel(uri = it)
                }
            }.awaitAll()
            _selectedImagesProcessing.value = false
        }

    }

    fun syncStorageCategory() {
        viewModelScope.launch {
            _categoryStorage.value = getCategoryStorageUseCase.launch(BaseUseCase.Parameters())
        }
    }

    private fun onContinueOptionClick(
        resolution: Float,
        quality: Float,
        deleteOriginal: Boolean
    ) {
        _allImageCompressOptions.value = ImageCompressionOptions(
            resolution = resolution,
            quality = quality,
            deleteOriginal = deleteOriginal
        )
    }

    private fun onImageCompressionOptionsConfirm(
        resolution: Float,
        quality: Float,
        deleteOriginal: Boolean
    ) {
        viewModelScope.launch {
            val originalUris = selectedImages.value.map {
                it.uri
            }
            val compressedFiles = compressAndSaveImagesUseCase.launch(
                CompressAndSaveImagesUseCase.Params(
                    context = CompressApplication.appContext,
                    uris = originalUris,
                    resolution = resolution,
                    quality = quality,
                    deleteOriginal = deleteOriginal
                )
            )
            if (deleteOriginal) {
                viewModelScope.launch {
                    originalUris.forEach {
                        deleteOriginalUseCase.launch(
                            DeleteOriginalUseCase.Parameters(
                                filePath = it.toFile().absolutePath
                            )
                        )
                    }
                }
            }
            sendEvent(Event.CompressionCompleted)
            val originalToCompressedMap = originalUris.zip(compressedFiles)

            addLibraryItemUseCase.launch(
                AddLibraryItemUseCase.Parameters(
                    libraryModels = originalToCompressedMap.map { (originalUri, compressedUri) ->
                        LibraryModel(
                            originalURI = originalUri,
                            compressedURI = compressedUri
                        )
                    }
                )
            )
        }
    }

    private fun onVideoCompressionOptionsConfirm(
    ) {
        viewModelScope.launch {
            compressAndSaveVideosUseCase.launch(
                CompressAndSaveVideoUseCase.Params(
                    uris = selectedVideos.value.map {
                        it.uri
                    }
                )
            )
            sendEvent(Event.CompressionCompleted)
        }
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.Images.RemoveImageClicked -> {
                _selectedImages.value = _selectedImages.value.filter {
                    it.uri.path != event.path
                }
            }

            is UIEvent.Images.ImageCompressionOptionsApplied -> {
                onContinueOptionClick(
                    event.resolution,
                    event.quality,
                    event.deleteOriginal
                )
            }

            is UIEvent.Navigate -> {
                _currentRoute.value = event.route
            }

            is UIEvent.Images.OnImagesAdded -> {
                onImageAdded(event.uris)
            }

            is UIEvent.Videos.OnVideosAdded -> {
                onVideoSelected(event.uris)
            }

            is UIEvent.Videos.RemoveVideoClicked -> {
                _selectedVideos.value = _selectedVideos.value.filter {
                    it.uri.path != event.path
                }
            }

            is UIEvent.Videos.VideoCompressionOptionsApplied -> TODO()
        }
    }

    private fun sendEvent(event: Event) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    companion object {
        const val INITIAL_RESOLUTION = 0.9f
        const val INITIAL_QUALITY = 0.9f
    }

    data class ImageCompressionOptions(
        val resolution: Float = INITIAL_RESOLUTION,
        val quality: Float = INITIAL_QUALITY,
        val deleteOriginal: Boolean = false
    )
}