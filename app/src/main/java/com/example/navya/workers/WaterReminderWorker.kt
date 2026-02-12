package com.example.navya.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WaterReminderWorker
@AssistedInject
constructor(@Assisted appContext: Context, @Assisted workerParams: WorkerParameters) :
        CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val plantName = inputData.getString("plant_name") ?: "Your plant"

        showNotification(plantName)

        return Result.success()
    }

    private fun showNotification(plantName: String) {
        val context = applicationContext
        val channelId = "water_reminder_channel"
        val notificationId = System.currentTimeMillis().toInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Cannot show notification without permission
                return
            }
        }

        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                    NotificationChannel(
                            channelId,
                            "Water Reminders",
                            NotificationManager.IMPORTANCE_HIGH
                    )
            notificationManager.createNotificationChannel(channel)
        }

        val intent =
                android.content.Intent(context, com.example.navya.MainActivity::class.java).apply {
                    flags =
                            android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                                    android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("navigate_to", "reminders_screen")
                }

        val pendingIntent: android.app.PendingIntent =
                android.app.PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        android.app.PendingIntent.FLAG_IMMUTABLE
                )

        val notification =
                NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(
                                android.R.drawable.ic_menu_today
                        ) // Replace with app icon if available
                        .setContentTitle("💧 Time to water $plantName")
                        .setContentText("Tap to open details and mark as watered.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent) // Tap behavior
                        .setAutoCancel(true)
                        .build()

        notificationManager.notify(notificationId, notification)
    }
}
