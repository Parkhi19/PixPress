package com.notesapp.compressify.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.notesapp.compressify.CompressApplication


fun Uri.createImageThumbnail(): Bitmap {
    val thumbnail =
        MediaStore.Images.Media.getBitmap(CompressApplication.contentResolver, this).run {
            Bitmap.createScaledBitmap(this, width / 20, height / 20, false)
        }
    return thumbnail
}

fun Uri.createVideoThumbnail(): Bitmap {
    return try {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(CompressApplication.appContext, this)
        mediaMetadataRetriever.frameAtTime?: Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)
    } catch (e:Exception) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)
    }
}

fun Uri.getFileName(): String {
    val returnCursor = CompressApplication.contentResolver.query(this, null, null, null, null)!!
    val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    returnCursor.close()
    return name ?: ""
}

fun Uri.getFileSize(): Long {
    val fileDescriptor = CompressApplication.contentResolver.openAssetFileDescriptor(this, "r")
    val fileSize = fileDescriptor?.length ?: 0
    fileDescriptor?.close()
    return fileSize
}

fun Long.getFormattedSize(): String {
    val kb = (this / 1024.0)
    val mb = kb / 1024f
    val gb = mb / 1024f

    return when {
        gb >= 1 -> {
            String.format("%.1f GB", gb)
        }

        mb >= 1 -> {
            String.format("%.1f MB", mb)
        }

        else -> {
            String.format("%.1f KB", kb)
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
    internalQuery?.use { cursor->
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

