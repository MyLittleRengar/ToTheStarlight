package com.project.tothestarlight.utility

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import java.util.Calendar

object AlarmUtils {
    fun setAlarmAt(context: Context, year: Int, month: Int, day: Int, code: Int, title: String, content: String) {
        val calendar = Calendar.getInstance().apply {
            set(year, month, day, 0, 5, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val triggerTimeMillis = calendar.timeInMillis
        Log.d("AlarmUtils", "Trigger Time in millis: $triggerTimeMillis")

        if (!hasExactAlarmPermission(context)) {
            // 권한 요청 화면으로 이동
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
            return
        }

        scheduleAlarm(context, triggerTimeMillis, code, title, content)
    }

    private fun hasExactAlarmPermission(context: Context): Boolean {
        return context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
    }

    private fun scheduleAlarm(context: Context, triggerTimeMillis: Long, code: Int, title: String, content: String) {
        val alarmIntent = Intent(context, MyAlarmReceiver::class.java).apply {
            putExtra("code", code)
            putExtra("title", title)
            putExtra("content", content)
        }

        val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
    }
}

