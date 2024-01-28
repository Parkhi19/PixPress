package com.notesapp.compressify.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.notesapp.compressify.CompressApplication
import com.notesapp.compressify.domain.model.MediaCategory
import java.io.File


fun Uri.createImageThumbnail(reduceFactor: Int = 20): Bitmap {
    val thumbnail = getBitmap().run {
            Bitmap.createScaledBitmap(this, width / reduceFactor, height / reduceFactor, false)
        }
    return thumbnail
}

fun Uri.getBitmap(): Bitmap {
    val bitmap = BitmapFactory.decodeFile(toFile().absolutePath)
    val exifInterface = ExifInterface(toFile().absolutePath)
    val orientation = exifInterface.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> {
            bitmap.rotate(90f)
        }

        ExifInterface.ORIENTATION_ROTATE_180 -> {
            bitmap.rotate(180f)
        }

        ExifInterface.ORIENTATION_ROTATE_270 -> {
            bitmap.rotate(270f)
        }

        else -> {
            bitmap
        }
    }
}

fun Bitmap.rotate(angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Uri.createVideoThumbnail(): Bitmap {
    return try {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(CompressApplication.appContext, this)
        mediaMetadataRetriever.frameAtTime ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)
    } catch (e: Exception) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)
    }
}

fun Uri.getFileName(): String {
    return toFile().name
}

fun Uri.getFileSize(): Long {
  return toFile().length()
}

fun Long.getFormattedSize(): String {
    val kb = (this / 1024.0)
    val mb = kb / 1024f
    val gb = mb / 1024f

    return when {
        gb >= 1 -> {
            "${gb.precised(1)} GB"
        }

        mb >= 1 -> {
            "${mb.precised(1)} MB"
        }

        else -> {
            "${kb.precised(1)} KB"
        }
    }
}

fun Context.getAllAudioFilesSize(): Long {
    var totalAudioSize = 0L
    val projection = arrayOf(
        MediaStore.Audio.Media.SIZE
    )
    val externalQuery = contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )
    val internalQuery = contentResolver.query(
        MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )
    internalQuery?.use { cursor ->
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
        while (cursor.moveToNext()) {

            val size = cursor.getLong(sizeColumn)
            totalAudioSize += size
        }
    }
    externalQuery?.use { cursor ->
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
        while (cursor.moveToNext()) {

            val size = cursor.getLong(sizeColumn)
            totalAudioSize += size
        }
    }
    return totalAudioSize
}

fun Context.getAllVideoFilesSize(): Long {
    var totalVideoSize = 0L
    val projection = arrayOf(
        MediaStore.Video.Media.SIZE
    )
    val externalQuery = contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )
    val internalQuery = contentResolver.query(
        MediaStore.Video.Media.INTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )
    internalQuery?.use { cursor ->
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
        while (cursor.moveToNext()) {

            val size = cursor.getLong(sizeColumn)
            totalVideoSize += size
        }
    }
    externalQuery?.use { cursor ->
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
        while (cursor.moveToNext()) {

            val size = cursor.getLong(sizeColumn)
            totalVideoSize += size
        }
    }
    return totalVideoSize
}

fun Context.getAllImageFilesSize(): Long {
    var totalImageSize = 0L
    val projection = arrayOf(
        MediaStore.Images.Media.SIZE
    )
    val externalQuery = contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )
    val internalQuery = contentResolver.query(
        MediaStore.Images.Media.INTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )
    internalQuery?.use { cursor ->
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
        while (cursor.moveToNext()) {

            val size = cursor.getLong(sizeColumn)
            totalImageSize += size
        }
    }
    externalQuery?.use { cursor ->
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
        while (cursor.moveToNext()) {

            val size = cursor.getLong(sizeColumn)
            totalImageSize += size
        }
    }
    return totalImageSize
}

fun Context.getAllDocumentSize(): Long {
    var totalDocumentSize = 0L
    val projection = arrayOf(
        MediaStore.Files.FileColumns.SIZE
    )
    val selection = "${MediaStore.Audio.Media.MIME_TYPE} == ?"
    val selectionArgs = arrayOf("application/pdf")
    val externalQuery = contentResolver.query(
        MediaStore.Files.getContentUri("external"),
        projection,
        selection,
        selectionArgs,
        null
    )
    val internalQuery = contentResolver.query(
        MediaStore.Files.getContentUri("internal"),
        projection,
        selection,
        selectionArgs,
        null
    )
    internalQuery?.use { cursor ->
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
        while (cursor.moveToNext()) {

            val size = cursor.getLong(sizeColumn)
            totalDocumentSize += size
        }
    }
    externalQuery?.use { cursor ->
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
        while (cursor.moveToNext()) {

            val size = cursor.getLong(sizeColumn)
            totalDocumentSize += size
        }
    }
    return totalDocumentSize
}

fun Context.getOtherFilesSize(): Long {
    return FileUtil.occupiedStorageSize - getAllAudioFilesSize() - getAllVideoFilesSize() - getAllImageFilesSize() - getAllDocumentSize()
}

fun Float.precised(precision: Int): Float {
    return String.format("%.${precision}f", this).toFloat()
}

fun Double.precised(precision: Int): Double {
    return String.format("%.${precision}f", this).toDouble()
}

fun Uri.getAbsoluteImagePath(): Uri? {
    val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getAbsolutePathAboveAPI29(MediaCategory.IMAGE)
    } else {
        getAbsolutePathBelowAPI29(MediaCategory.IMAGE)
    }
    return path?.let {
        File(it).toUri()
    }
}

fun Uri.getAbsoluteVideoPath(): Uri? {
    val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getAbsolutePathAboveAPI29(MediaCategory.VIDEO)
    } else {
        getAbsolutePathBelowAPI29(MediaCategory.VIDEO)
    }
    return path?.let {
        File(it).toUri()
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun Uri.getAbsolutePathAboveAPI29(
    category: MediaCategory
): String? {
    val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
    val cursor = CompressApplication.contentResolver.query(this, projection, null, null, null)
    val displayID = cursor?.use {
        it.moveToFirst()
        val index = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        it.getString(index).substringBeforeLast(".")
    }
    return queryData(
        category,
        MediaStore.MediaColumns._ID,
        displayID
    )
}

private fun Uri.getAbsolutePathBelowAPI29(category: MediaCategory): String? {
    val projection = arrayOf(MediaStore.MediaColumns.DOCUMENT_ID)
    val cursor = CompressApplication.contentResolver.query(this, projection, null, null, null)
    return if (cursor != null) {
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DOCUMENT_ID)
        cursor.moveToFirst()
        val documentId = cursor.getString(columnIndex)
        cursor.close()
        queryData(
            category,
            MediaStore.MediaColumns.DOCUMENT_ID,
            documentId
        )
    } else {
        null
    }
}

private fun queryData(
    category: MediaCategory,
    selectionField: String,
    selectionId: String?
): String? {
    val resultProjection = arrayOf(MediaStore.MediaColumns.DATA)
    val queryUri = when (category) {
        MediaCategory.IMAGE -> {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        MediaCategory.VIDEO -> {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
    }
    val resultCursor = CompressApplication.contentResolver.query(
        queryUri,
        resultProjection,
        "$selectionField = $0",
        arrayOf(selectionId),
        null
    )
    return resultCursor?.use {
        it.moveToFirst()
        val path = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
        path
    }
}

