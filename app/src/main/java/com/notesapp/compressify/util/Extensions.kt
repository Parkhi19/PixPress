package com.notesapp.compressify.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.notesapp.compressify.CompressApplication


fun Uri.createThumbnail(): Bitmap {
    val thumbnail =
        MediaStore.Images.Media.getBitmap(CompressApplication.contentResolver, this).run {
            Bitmap.createScaledBitmap(this, width / 10, height / 10, false)
        }
    return thumbnail
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
    val query = contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )
    query?.use { cursor ->
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
    val query = contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )
    query?.use { cursor ->
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
    val query = contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )
    query?.use { cursor ->
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
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.MIME_TYPE
    )
    val selection = "${MediaStore.Audio.Media.MIME_TYPE} == ?"
    val selectionArgs = arrayOf("application/pdf")
    val query = contentResolver.query(
        MediaStore.Files.getContentUri("external"),
        projection,
        selection,
        selectionArgs,
        null
    )
    query?.use { cursor ->
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

