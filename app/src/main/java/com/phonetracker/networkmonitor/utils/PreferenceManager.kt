package com.phonetracker.networkmonitor.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_TRACKING_ENABLED = "tracking_enabled"
        private const val KEY_SCHEDULER_ENABLED = "scheduler_enabled"
        private const val KEY_SPREADSHEET_ID = "spreadsheet_id"
        private const val KEY_GOOGLE_ACCOUNT = "google_account"
    }
    
    fun isTrackingEnabled(): Boolean {
        return prefs.getBoolean(KEY_TRACKING_ENABLED, false)
    }
    
    fun setTrackingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_TRACKING_ENABLED, enabled).apply()
    }
    
    fun isSchedulerEnabled(): Boolean {
        return prefs.getBoolean(KEY_SCHEDULER_ENABLED, false)
    }
    
    fun setSchedulerEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SCHEDULER_ENABLED, enabled).apply()
    }
    
    fun getSpreadsheetId(): String? {
        return prefs.getString(KEY_SPREADSHEET_ID, null)
    }
    
    fun setSpreadsheetId(spreadsheetId: String) {
        prefs.edit().putString(KEY_SPREADSHEET_ID, spreadsheetId).apply()
    }
    
    fun getGoogleAccount(): String? {
        return prefs.getString(KEY_GOOGLE_ACCOUNT, null)
    }
    
    fun setGoogleAccount(accountName: String) {
        prefs.edit().putString(KEY_GOOGLE_ACCOUNT, accountName).apply()
    }
}