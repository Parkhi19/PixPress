package com.notesapp.compressify

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class CompressApplication : Application() {

    private val supervisorJob = SupervisorJob()
    val applicationScope = CoroutineScope(supervisorJob)
    override fun onCreate() {
        super.onCreate()
        App = this
        appContext = applicationContext
    }

    companion object {

        lateinit var App: CompressApplication
            private set
        lateinit var appContext: Context
            private set
        val contentResolver: ContentResolver
            get() = appContext.contentResolver
    }
}