package com.notesapp.compressify.domain.useCase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.notesapp.compressify.domain.model.CompressionRatio

class CompressAndSaveImagesUseCase : BaseUseCase<CompressAndSaveImagesUseCase.Params>() {
    data class Params(
        val context: Context,
        val uri: Uri,
        val compressionRatio: CompressionRatio
    ) : BaseUseCase.Parameters()

    override suspend fun launch(parameters: Params) {
        val bitmap = MediaStore.Images.Media.getBitmap(parameters.context.contentResolver, parameters.uri)
        val compressedHeight = bitmap.height * parameters.compressionRatio.ratio
        val compressedWidth = bitmap.width * parameters.compressionRatio.ratio
        val compressedBitmap = Bitmap.createScaledBitmap(bitmap, compressedWidth.toInt() ,compressedHeight.toInt(), false)
        val path = MediaStore.Images.Media.insertImage(
            parameters.context.contentResolver,
            compressedBitmap,
            "Title",
            null
        )
    }
}