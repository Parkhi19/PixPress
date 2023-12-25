package com.notesapp.compressify.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesapp.compressify.CompressApplication
import com.notesapp.compressify.domain.model.CompressionRatio
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.domain.useCase.CompressAndSaveImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val compressAndSaveImagesUseCase: CompressAndSaveImagesUseCase
): ViewModel() {
    private val _selectedImages = MutableStateFlow<List<ImageModel>>(emptyList())
    val selectedImages = _selectedImages.asStateFlow()


    fun compressAndSaveImages() {
        viewModelScope.launch {
            compressAndSaveImagesUseCase.launch(
                CompressAndSaveImagesUseCase.Params(
                    context = CompressApplication.appContext,
                    uris = selectedImages.value.map {
                        it.uri
                    },
                    compressionRatio = CompressionRatio.MEDIUM
                )
            )
        }
    }

    fun onImageSelected(uris: List<Uri>){
        _selectedImages.value = uris.map{
            ImageModel(uri = it)
        }
    }
}