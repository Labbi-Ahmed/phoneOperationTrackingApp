package com.phonetracker.networkmonitor.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.phonetracker.networkmonitor.worker.GoogleSheetsUploadWorker
import com.phonetracker.networkmonitor.utils.AlarmScheduler

class UploadAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("UploadAlarmReceiver", "Alarm fired: scheduling upload")
        val request = OneTimeWorkRequestBuilder<GoogleSheetsUploadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "AlarmTriggeredUpload",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            request
        )

        // Schedule the next alarm to keep the 10-minute cadence
        AlarmScheduler.schedule(context)
    }
}
