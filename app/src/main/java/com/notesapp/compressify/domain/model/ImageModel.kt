package com.notesapp.compressify.domain.model

import android.graphics.Bitmap
import android.net.Uri
import com.notesapp.compressify.util.createThumbnail
import com.notesapp.compressify.util.getFileName
import com.notesapp.compressify.util.getFileSize

data class ImageModel(
    val uri: Uri,
    val name: String,
    val size: Long,
    val thumbnail: Bitmap
) {
    constructor(uri: Uri) : this(uri, uri.getFileName(), uri.getFileSize(), uri.createThumbnail())

}
