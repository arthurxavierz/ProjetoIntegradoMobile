package com.example.cicloestudos3.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.cicloestudos3.MainActivity
import com.example.cicloestudos3.R

object NotificationHelper {

    const val CHANNEL_ID   = "revision_reminders"
    const val CHANNEL_NAME = "Lembretes de Revisão"

    fun createChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificações de revisão agendada"
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }

    fun sendRevisionNotification(
        context: Context,
        notificationId: Int,
        topicTitle: String,
        subjectName: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Hora de revisar!")
            .setContentText("$topicTitle — $subjectName")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Sua revisão de \"$topicTitle\" em $subjectName está agendada para agora. Bons estudos!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.notify(notificationId, notification)
    }
}
