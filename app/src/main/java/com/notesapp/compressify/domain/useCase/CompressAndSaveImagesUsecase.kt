package com.notesapp.compressify.domain.useCase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.notesapp.compressify.CompressApplication
import com.notesapp.compressify.service.ImageCompressionService
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import com.notesapp.compressify.util.FileUtil
import kotlinx.coroutines.delay
import java.io.FileOutputStream
import javax.inject.Inject

class CompressAndSaveImagesUseCase @Inject constructor(): BaseUseCase<CompressAndSaveImagesUseCase.Params, Unit>() {
    data class Params(
        val imagesToOptions : List<Pair<Uri, MainViewModel.ImageCompressionOptions>>,
    ) : Parameters()

    override suspend fun launch(parameters: Params) {
        val intent = ImageCompressionService.getIntent(
            context = CompressApplication.appContext,
            imagesToOptions = parameters.imagesToOptions
        )
        ContextCompat.startForegroundService(CompressApplication.appContext, intent)
    }
}