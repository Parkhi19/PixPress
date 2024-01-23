package com.notesapp.compressify.domain.model

import android.graphics.Bitmap
import android.net.Uri
import com.notesapp.compressify.util.createVideoThumbnail
import com.notesapp.compressify.util.getFileName
import com.notesapp.compressify.util.getFileSize

data class VideoModel(
    val uri: Uri,
    val name: String,
    val size: Long,
    val thumbnail: Bitmap
) {
    constructor(uri: Uri) : this(uri, uri.getFileName(), uri.getFileSize(), uri.createVideoThumbnail())
//    constructor(uri: Uri) : this(uri, uri.getFileName(), uri.getFileSize(), uri.createThumbnail())
}