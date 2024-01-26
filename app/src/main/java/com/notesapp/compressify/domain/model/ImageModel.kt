package com.notesapp.compressify.domain.model

import android.graphics.Bitmap
import android.net.Uri
import com.notesapp.compressify.util.createImageThumbnail
import com.notesapp.compressify.util.getBitmap
import com.notesapp.compressify.util.getFileName
import com.notesapp.compressify.util.getFileSize

data class ImageModel(
    val uri: Uri,
    val name: String,
    val size: Long,
    val thumbnail: Bitmap
) {
    constructor(uri: Uri) : this(uri, uri.getFileName(), uri.getFileSize(), uri.createImageThumbnail())

    val height = uri.getBitmap().height
    val width = uri.getBitmap().width

}
