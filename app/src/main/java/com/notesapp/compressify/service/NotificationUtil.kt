package com.notesapp.compressify.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.notesapp.compressify.R

object NotificationUtil  {

    private const val COMPLETION_NOTIFICATION_ID = "Image Compression Completed"
    fun createCompletionNotification(context: Context, message: String): Notification {
        createNotificationChannel(context, COMPLETION_NOTIFICATION_ID)
        return NotificationCompat.Builder(context, COMPLETION_NOTIFICATION_ID)
            .setContentTitle(message)
            .setSmallIcon(R.drawable.completed)
            .build()
    }

    private fun createNotificationChannel(context: Context, channel: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channel, channel,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}