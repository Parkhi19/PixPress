package com.notesapp.compressify.util

import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.notesapp.compressify.CompressApplication


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