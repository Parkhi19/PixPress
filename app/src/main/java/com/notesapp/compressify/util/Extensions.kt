package com.notesapp.compressify.util

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.notesapp.compressify.CompressApplication
import kotlin.math.roundToInt


fun Uri.createThumbnail(): Bitmap {
    val thumbnail = MediaStore.Images.Media.getBitmap(CompressApplication.contentResolver, this).run {
        Bitmap.createScaledBitmap(this, width/ 10, height/ 10, false)
    }
    return thumbnail
}

fun Uri.getFileName(): String {
    val returnCursor = CompressApplication.contentResolver.query(this, null, null, null, null)!!
    val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    returnCursor.close()
    return name?:""
}

fun Uri.getFileSize():Long{
    val fileDescriptor = CompressApplication.contentResolver.openAssetFileDescriptor(this, "r")
    val fileSize = fileDescriptor?.length?:0
    fileDescriptor?.close()
    return fileSize
}

fun Long.getFormattedSize(): String {
    val kb = (this / 1024.0)
    val mb = kb / 1024f

    return if (mb > 0) {
        String.format("%.1f MB", mb)
    } else {
        String.format("%.1f KB", kb)
    }
}
