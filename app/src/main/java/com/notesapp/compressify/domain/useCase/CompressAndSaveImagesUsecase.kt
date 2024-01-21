package com.notesapp.compressify.domain.useCase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toFile
import com.notesapp.compressify.domain.model.CompressionRatio
import com.notesapp.compressify.util.FileUtil
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

class CompressAndSaveImagesUseCase @Inject constructor(): BaseUseCase<CompressAndSaveImagesUseCase.Params, Unit>() {
    data class Params(
        val context: Context,
        val uris: List<Uri>,
        val resolution : Float,
        val quality : Float,
        val keepOriginal : Boolean
    ) : Parameters()

    override suspend fun launch(parameters: Params) {
        parameters.uris.forEach {
            val bitmap = MediaStore.Images.Media.getBitmap(parameters.context.contentResolver, it)
            val compressedHeight = bitmap.height * parameters.resolution
            val compressedWidth = bitmap.width * parameters.resolution
            val compressedBitmap = Bitmap.createScaledBitmap(bitmap, compressedWidth.toInt() ,compressedHeight.toInt(), false)

            val outputStream = FileOutputStream(FileUtil.getNewImageFile(".jpg"))
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, (parameters.quality*100).toInt(), outputStream)
            outputStream.flush()
            outputStream.close()
        }

    }
}