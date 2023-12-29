package com.notesapp.compressify.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesapp.compressify.CompressApplication
import com.notesapp.compressify.domain.model.Event
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.domain.useCase.CompressAndSaveImagesUseCase
import com.notesapp.compressify.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val compressAndSaveImagesUseCase: CompressAndSaveImagesUseCase
) : ViewModel() {
    private val _selectedImages = MutableStateFlow<List<ImageModel>>(emptyList())
    val selectedImages = _selectedImages.asStateFlow()

    private val eventChannel = Channel<Event> {  }
    val eventsFlow = eventChannel.receiveAsFlow()

    fun onImageSelected(uris: List<Uri>) {
        viewModelScope.launch{
            _selectedImages.value = uris.map {
                async {
                    ImageModel(uri = it)
                }
            }.awaitAll()
        }

    }



    fun onImageCompressionOptionsConfirm(resolution: Float, quality: Float, keepOriginal: Boolean) {
        viewModelScope.launch {
            compressAndSaveImagesUseCase.launch(
                CompressAndSaveImagesUseCase.Params(
                    context = CompressApplication.appContext,
                    uris = selectedImages.value.map {
                        it.uri
                    },
                    resolution = resolution,
                    quality = quality,
                    keepOriginal = keepOriginal
                )
            )
            sendEvent(Event.CompressionCompleted)
        }
    }

    fun  onUIEvent(event: UIEvent) {
        when(event) {
            is UIEvent.Images.RemoveImageClicked -> {
                _selectedImages.value = _selectedImages.value.filter {
                    it.uri.path != event.path
                }
            }
            is UIEvent.Images.CompressionOptionsConfirmed -> {
                onImageCompressionOptionsConfirm(event.resolution, event.quality, event.keepOriginal)
            }
        }
    }

   private fun sendEvent(event: Event) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}