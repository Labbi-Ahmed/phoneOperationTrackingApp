package com.phonetracker.networkmonitor.data

import android.content.Context
import android.util.Log
import com.phonetracker.networkmonitor.model.NetworkRequest
import kotlinx.coroutines.*
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class NetworkLogManager(private val context: Context) {
    
    private val logFile: File by lazy {
        File(context.getExternalFilesDir(null), "network_logs.csv")
    }
    
    private val logScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isInitialized = false
    
    companion object {
        private const val TAG = "NetworkLogManager"
    }
    
    init {
        initializeLogFile()
    }
    
    private fun initializeLogFile() {
        logScope.launch {
            try {
                if (!logFile.exists()) {
                    logFile.createNewFile()
                    // Write header
                    FileWriter(logFile, false).use { writer ->
                        writer.write(NetworkRequest.getCSVHeader() + "\n")
                    }
                    Log.d(TAG, "Log file created: ${logFile.absolutePath}")
                }
                isInitialized = true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize log file", e)
            }
        }
    }
    
    fun logRequest(request: NetworkRequest) {
        if (!isInitialized) {
            Log.w(TAG, "Log manager not initialized yet")
            return
        }
        
        logScope.launch {
            try {
                FileWriter(logFile, true).use { writer ->
                    writer.write(request.toCSVRow() + "\n")
                }
                Log.d(TAG, "Logged request: ${request.url}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to log request", e)
            }
        }
    }
    
    fun getLogFile(): File = logFile
    
    fun getLogContent(): String {
        return try {
            if (logFile.exists()) {
                logFile.readText()
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read log content", e)
            ""
        }
    }
    
    fun clearLogs() {
        logScope.launch {
            try {
                if (logFile.exists()) {
                    logFile.delete()
                }
                initializeLogFile()
                Log.d(TAG, "Logs cleared")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear logs", e)
            }
        }
    }
    
    fun getLogStats(): LogStats {
        return try {
            val content = getLogContent()
            val lines = content.split("\n").filter { it.isNotBlank() }
            val requestCount = if (lines.isNotEmpty()) lines.size - 1 else 0 // Exclude header
            
            LogStats(
                totalRequests = requestCount,
                fileSize = logFile.length(),
                lastModified = logFile.lastModified()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get log stats", e)
            LogStats(0, 0, 0)
        }
    }
}

data class LogStats(
    val totalRequests: Int,
    val fileSize: Long,
    val lastModified: Long
)