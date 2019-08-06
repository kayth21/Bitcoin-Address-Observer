package com.ceaver.bao.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.ceaver.bao.Application
import com.ceaver.bao.MainActivity
import com.ceaver.bao.R
import kotlin.random.Random

object Notification {

    private const val CHANNEL_ID = "com.ceaver.bao.notification.Notification.ChannelId"

    init {
        val name = "Bitcoin Address Observer notification channel"
        val description = "Notification on bitcoin address tx count change"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply { this.description = description }
        val notificationManager = getSystemService(Application.appContext!!, NotificationManager::class.java)!!
        notificationManager.createNotificationChannel(channel)
    }

    fun notifyStatusChange(title: String, text: String, image: Int) {
        val intent = Intent(Application.appContext!!, MainActivity::class.java).apply { this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        val pendingIntent = PendingIntent.getActivity(Application.appContext!!, 0, intent, 0)

        val notification = NotificationCompat.Builder(Application.appContext!!, CHANNEL_ID)
            .setSmallIcon(R.drawable.bitcoin_notification_icon)
            .setLargeIcon(BitmapFactory.decodeResource(Application.appContext!!.resources, image))
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(Application.appContext!!).notify(Random.nextInt(), notification);
    }
}