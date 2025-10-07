package com.phonetracker.networkmonitor.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.phonetracker.networkmonitor.receiver.UploadAlarmReceiver

object AlarmScheduler {
    private const val INTERVAL_MILLIS = 10 * 60 * 1000L // 10 minutes
    private const val REQUEST_CODE = 1010

    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(context)
        val triggerAt = System.currentTimeMillis() + INTERVAL_MILLIS

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent
                )
            }
            Log.d("AlarmScheduler", "Scheduled next upload alarm in 10 minutes")
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Failed to schedule alarm", e)
        }
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(createPendingIntent(context))
        Log.d("AlarmScheduler", "Cancelled upload alarm")
    }

    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, UploadAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
