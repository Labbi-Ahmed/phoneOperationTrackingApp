package com.phonetracker.networkmonitor.model

import java.text.SimpleDateFormat
import java.util.*

data class NetworkRequest(
    val timestamp: Long,
    val protocol: String, // TCP, UDP, etc.
    val sourceAddress: String,
    val sourcePort: Int,
    val destAddress: String,
    val destPort: Int,
    val url: String,
    val service: String // HTTP, HTTPS, DNS, etc.
) {
    fun toCSVRow(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedTime = dateFormat.format(Date(timestamp))
        
        return "$formattedTime,$protocol,$sourceAddress:$sourcePort,$destAddress:$destPort,$url,$service"
    }
    
    companion object {
        fun getCSVHeader(): String {
            return "Timestamp,Protocol,Source,Destination,URL,Service"
        }
    }
}