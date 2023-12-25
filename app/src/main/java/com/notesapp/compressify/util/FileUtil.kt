package com.notesapp.compressify.util

import android.os.Environment
import com.notesapp.compressify.CompressApplication
import java.io.File

object FileUtil {
    fun getNewImageFile(fileExtension : String):File{
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Compressify").apply {
            if(!exists()){
                mkdirs()
            }
        }
        return File.createTempFile("IMG_", fileExtension, directory)
    }
}