package com.notesapp.compressify.domain.useCase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.notesapp.compressify.CompressApplication
import com.notesapp.compressify.service.ImageCompressionService
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import com.notesapp.compressify.util.FileUtil
import com.notesapp.compressify.util.getBitmap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.FileOutputStream
import javax.inject.Inject

class CompressAndSaveImagesUseCase @Inject constructor() :
    BaseUseCase<CompressAndSaveImagesUseCase.Params, Int>() {
    data class Params(
        val imagesToOptions: List<ImageCompressionService.ImageCompressionModel>
    ) : Parameters()

    override suspend fun launch(parameters: Params): Int {
        coroutineScope {
            parameters.imagesToOptions.chunked(10).forEach { chunkedImages ->
                chunkedImages.map { compressionModel ->
                    async { compressImage(compressionModel) }
                }.awaitAll()
            }
        }
        return parameters.imagesToOptions.size
    }

    override fun launchWithFlow(parameters: Params): Flow<Int> = flow {
        val chunkedList = parameters.imagesToOptions.chunked(10)
        emit(0)
        coroutineScope {
            var compressed = 0
            chunkedList.forEach { chunkedImages ->
                chunkedImages.map { compressionModel ->
                    async { compressImage(compressionModel) }
                }.awaitAll()
                chunkedImages.filter { it.deleteOriginal }.map {
                    async {
                        it.uri.toFile().delete()
                    }
                }.awaitAll()
                compressed += chunkedImages.size
                emit(compressed)
            }
        }
    }

    private fun compressImage(compressionModel: ImageCompressionService.ImageCompressionModel): Uri {
        val originalExtension = compressionModel.uri.toFile().extension

        val bitmap = compressionModel.uri.getBitmap()
        val compressedHeight = bitmap.height * compressionModel.resolution
        val compressedWidth = bitmap.width * compressionModel.resolution
        val compressedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            compressedWidth.toInt(),
            compressedHeight.toInt(),
            false
        )

        val resultFile = FileUtil.getNewImageFile(".$originalExtension")
        val outputStream = FileOutputStream(resultFile)
        val compressionType = when (originalExtension) {
            "jpg", "jpeg" -> Bitmap.CompressFormat.JPEG
            "png" -> Bitmap.CompressFormat.PNG
            "webp" -> Bitmap.CompressFormat.WEBP
            else -> Bitmap.CompressFormat.JPEG
        }
        compressedBitmap.compress(
            compressionType,
            (compressionModel.quality * 100).toInt(),
            outputStream
        )
        outputStream.flush()
        outputStream.close()
        return resultFile.toUri()
    }

}