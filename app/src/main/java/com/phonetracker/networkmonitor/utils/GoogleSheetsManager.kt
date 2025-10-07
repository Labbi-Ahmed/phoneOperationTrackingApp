package com.phonetracker.networkmonitor.utils

import android.accounts.Account
import android.content.Context
import android.util.Log
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.AccountPicker
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class GoogleSheetsManager(private val context: Context) {
    
    private val credential: GoogleAccountCredential by lazy {
        GoogleAccountCredential.usingOAuth2(
            context, 
            listOf(SheetsScopes.SPREADSHEETS)
        )
    }
    
    private val sheetsService: Sheets by lazy {
        Sheets.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("Phone Operation Tracking App")
            .build()
    }
    
    companion object {
        private const val TAG = "GoogleSheetsManager"
        private const val SPREADSHEET_ID_KEY = "spreadsheet_id"
        
        // You need to create a Google Sheets file and put its ID here
        // Or implement a way for users to input their own spreadsheet ID
        private const val DEFAULT_SPREADSHEET_ID = "YOUR_SPREADSHEET_ID_HERE" // Replace with actual ID
    }
    
    suspend fun appendToSheet(headerRow: String, logContent: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val spreadsheetId = getSpreadsheetId()
            if (spreadsheetId.isEmpty()) {
                Log.e(TAG, "No spreadsheet ID configured")
                return@withContext false
            }
            
            // Parse CSV content into rows
            val lines = logContent.split("\n").filter { it.isNotBlank() }
            val rows = mutableListOf<List<Any>>()
            
            // Add header row with timestamp
            rows.add(listOf(headerRow))
            rows.add(listOf("")) // Empty row for spacing
            
            // Add data rows
            lines.forEach { line ->
                val columns = line.split(",")
                rows.add(columns)
            }
            
            // Add empty row at the end
            rows.add(listOf(""))
            
            // Create the request
            val valueRange = ValueRange().setValues(rows)
            
            val request = sheetsService.spreadsheets().values()
                .append(spreadsheetId, "Sheet1!A:F", valueRange)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
            
            val response = request.execute()
            Log.d(TAG, "Successfully appended ${rows.size} rows to spreadsheet")
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to append to Google Sheets", e)
            false
        }
    }
    
    fun setAccount(account: Account) {
        credential.selectedAccount = account
    }
    
    private fun getSpreadsheetId(): String {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString(SPREADSHEET_ID_KEY, DEFAULT_SPREADSHEET_ID) ?: DEFAULT_SPREADSHEET_ID
    }
    
    fun setSpreadsheetId(spreadsheetId: String) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString(SPREADSHEET_ID_KEY, spreadsheetId).apply()
    }
    
    suspend fun createSpreadsheet(title: String): String? = withContext(Dispatchers.IO) {
        try {
            val spreadsheet = Spreadsheet().apply {
                properties = SpreadsheetProperties().apply {
                    this.title = title
                }
                sheets = listOf(
                    Sheet().apply {
                        properties = SheetProperties().apply {
                            this.title = "Network Logs"
                        }
                    }
                )
            }
            
            val request = sheetsService.spreadsheets().create(spreadsheet)
            val response = request.execute()
            
            val spreadsheetId = response.spreadsheetId
            Log.d(TAG, "Created new spreadsheet: $spreadsheetId")
            
            // Set up headers
            setupHeaders(spreadsheetId)
            
            spreadsheetId
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create spreadsheet", e)
            null
        }
    }
    
    private suspend fun setupHeaders(spreadsheetId: String) {
        try {
            val headers = listOf(
                listOf("Timestamp", "Protocol", "Source", "Destination", "URL", "Service")
            )
            
            val valueRange = ValueRange().setValues(headers)
            
            sheetsService.spreadsheets().values()
                .update(spreadsheetId, "Sheet1!A1:F1", valueRange)
                .setValueInputOption("RAW")
                .execute()
                
            Log.d(TAG, "Headers set up successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup headers", e)
        }
    }
}