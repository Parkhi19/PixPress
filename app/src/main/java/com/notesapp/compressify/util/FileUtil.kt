package com.notesapp.compressify.util

import android.os.Environment
import java.io.File

object FileUtil {
    fun getNewImageFile(fileExtension: String): File {
        val directory =
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "/PixPress/Images"
            ).apply {
                if (!exists()) {
                    mkdirs()
                }
            }
        return File.createTempFile("IMG_", fileExtension, directory)
    }

    val videoFilesDirectory: File
        get() {
            val directory =
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "/PixPress/Videos"
                ).apply {
                        if (!exists()) {
                            mkdirs()
                        }
                    }
            return directory
        }
}