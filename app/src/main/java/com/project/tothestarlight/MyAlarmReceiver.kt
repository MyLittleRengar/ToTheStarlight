package com.project.tothestarlight

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MyAlarmReceiver: BroadcastReceiver() {
    val CHANNEL_ID = "Test"
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val code = intent.extras?.get("code")
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")

        // 로그로 값 확인
        Log.d("MyAlarmReceiver", "Code: $code, Title: $title, Content: $content")

        if (code == MainActivity.REQUEST_CODE) {
            createNotificationChannel(context)

            val notificationTitle = title ?: "기본 제목"
            val notificationContent = content ?: "기본 내용"

            val mainIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(context, 101, mainIntent, PendingIntent.FLAG_IMMUTABLE)

            val builder01 = NotificationCompat.Builder(context, CHANNEL_ID).apply {
                setSmallIcon(R.drawable.moon_shape15)
                setContentTitle(notificationTitle)
                setContentText(notificationContent)
                priority = NotificationCompat.PRIORITY_DEFAULT
                setContentIntent(pendingIntent)
                setAutoCancel(true)
            }

            with(NotificationManagerCompat.from(context)) {
                notify(5, builder01.build())
            }
        }
    }


    private fun createNotificationChannel(context: Context?) {
        val name = "Astro"
        val descriptionText = "Astro information"
        val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = descriptionText
        }

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}