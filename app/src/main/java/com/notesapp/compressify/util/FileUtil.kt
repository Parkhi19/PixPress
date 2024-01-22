package com.notesapp.compressify.util

import android.os.Environment
import android.os.StatFs
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

    val totalStorageSize: Long
        get() {
            val stat = StatFs(Environment.getExternalStorageDirectory().path)
            return stat.blockSizeLong * stat.blockCountLong
        }

    val availableStorageSize: Long
        get() {
            val stat = StatFs(Environment.getExternalStorageDirectory().path)
            return stat.blockSizeLong * stat.availableBlocksLong
        }

    val occupiedStorageSize: Long
        get() {
            return totalStorageSize - availableStorageSize
        }

    val occupiedStoragePercentage: Float
        get() {
            return (occupiedStorageSize.toFloat() / totalStorageSize.toFloat()) * 100
        }
}

