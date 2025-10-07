package com.phonetracker.networkmonitor.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.phonetracker.networkmonitor.data.NetworkLogManager
import com.phonetracker.networkmonitor.utils.GoogleSheetsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class GoogleSheetsUploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "GoogleSheetsUploadWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val networkLogManager = NetworkLogManager(applicationContext)
            val googleSheetsManager = GoogleSheetsManager(applicationContext)
            
            // Get log content
            val logContent = networkLogManager.getLogContent()
            
            if (logContent.isBlank()) {
                Log.d(TAG, "No logs to upload")
                return@withContext Result.success()
            }
            
            // Add datetime header
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentTime = dateFormat.format(Date())
            val headerRow = "=== Upload Session: $currentTime ==="
            
            // Upload to Google Sheets
            val success = googleSheetsManager.appendToSheet(headerRow, logContent)
            
            if (success) {
                // Clear local logs after successful upload
                networkLogManager.clearLogs()
                Log.d(TAG, "Successfully uploaded logs to Google Sheets and cleared local logs")
                Result.success()
            } else {
                Log.e(TAG, "Failed to upload logs to Google Sheets")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during upload work", e)
            Result.failure()
        }
    }
}