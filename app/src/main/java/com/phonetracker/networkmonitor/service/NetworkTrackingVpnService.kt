package com.phonetracker.networkmonitor.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.phonetracker.networkmonitor.R
import com.phonetracker.networkmonitor.data.NetworkLogManager
import com.phonetracker.networkmonitor.model.NetworkRequest
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetAddress
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

class NetworkTrackingVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var isRunning = false
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var networkLogManager: NetworkLogManager
    
    companion object {
        private const val TAG = "NetworkTrackingVpn"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "vpn_service_channel"
    }

    override fun onCreate() {
        super.onCreate()
        networkLogManager = NetworkLogManager(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startVpn()
        return START_STICKY
    }

    private fun startVpn() {
        if (isRunning) return
        
        try {
            val builder = Builder()
                .setMtu(1500)
                .addAddress("10.0.0.2", 32)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("8.8.8.8")
                .addDnsServer("8.8.4.4")
                .setBlocking(true)
            
            vpnInterface = builder.establish()
            
            if (vpnInterface != null) {
                isRunning = true
                startForeground(NOTIFICATION_ID, createNotification())
                
                serviceScope.launch {
                    handlePackets()
                }
                
                Log.d(TAG, "VPN started successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start VPN", e)
        }
    }

    private suspend fun handlePackets() {
        val vpnInput = FileInputStream(vpnInterface!!.fileDescriptor)
        val vpnOutput = FileOutputStream(vpnInterface!!.fileDescriptor)
        val packet = ByteArray(32767)
        
        while (isRunning) {
            try {
                val length = vpnInput.read(packet)
                if (length > 0) {
                    // Parse packet and extract network request info
                    parsePacket(packet, length)
                    
                    // Forward packet (passthrough)
                    vpnOutput.write(packet, 0, length)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling packet", e)
                if (!isRunning) break
            }
        }
    }

    private fun parsePacket(packet: ByteArray, length: Int) {
        try {
            // Parse IP header
            val version = (packet[0].toInt() shr 4) and 0xF
            if (version != 4) return // Only handle IPv4
            
            val protocol = packet[9].toUByte().toInt()
            val sourceAddress = InetAddress.getByAddress(packet.sliceArray(12..15))
            val destAddress = InetAddress.getByAddress(packet.sliceArray(16..19))
            
            when (protocol) {
                6 -> parseTcpPacket(packet, sourceAddress, destAddress) // TCP
                17 -> parseUdpPacket(packet, sourceAddress, destAddress) // UDP
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing packet", e)
        }
    }

    private fun parseTcpPacket(packet: ByteArray, sourceAddress: InetAddress, destAddress: InetAddress) {
        try {
            val ipHeaderLength = (packet[0].toInt() and 0xF) * 4
            val sourcePort = ((packet[ipHeaderLength].toUByte().toInt() shl 8) + packet[ipHeaderLength + 1].toUByte().toInt())
            val destPort = ((packet[ipHeaderLength + 2].toUByte().toInt() shl 8) + packet[ipHeaderLength + 3].toUByte().toInt())
            
            val networkRequest = NetworkRequest(
                timestamp = System.currentTimeMillis(),
                protocol = "TCP",
                sourceAddress = sourceAddress.hostAddress ?: "",
                sourcePort = sourcePort,
                destAddress = destAddress.hostAddress ?: "",
                destPort = destPort,
                url = determineUrl(destAddress.hostAddress ?: "", destPort),
                service = determineService(destPort)
            )
            
            networkLogManager.logRequest(networkRequest)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing TCP packet", e)
        }
    }

    private fun parseUdpPacket(packet: ByteArray, sourceAddress: InetAddress, destAddress: InetAddress) {
        try {
            val ipHeaderLength = (packet[0].toInt() and 0xF) * 4
            val sourcePort = ((packet[ipHeaderLength].toUByte().toInt() shl 8) + packet[ipHeaderLength + 1].toUByte().toInt())
            val destPort = ((packet[ipHeaderLength + 2].toUByte().toInt() shl 8) + packet[ipHeaderLength + 3].toUByte().toInt())
            
            val networkRequest = NetworkRequest(
                timestamp = System.currentTimeMillis(),
                protocol = "UDP",
                sourceAddress = sourceAddress.hostAddress ?: "",
                sourcePort = sourcePort,
                destAddress = destAddress.hostAddress ?: "",
                destPort = destPort,
                url = determineUrl(destAddress.hostAddress ?: "", destPort),
                service = determineService(destPort)
            )
            
            networkLogManager.logRequest(networkRequest)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing UDP packet", e)
        }
    }

    private fun determineUrl(address: String, port: Int): String {
        return when (port) {
            80 -> "http://$address"
            443 -> "https://$address"
            else -> "$address:$port"
        }
    }

    private fun determineService(port: Int): String {
        return when (port) {
            80, 8080 -> "HTTP"
            443, 8443 -> "HTTPS"
            53 -> "DNS"
            21 -> "FTP"
            22 -> "SSH"
            23 -> "Telnet"
            25 -> "SMTP"
            110 -> "POP3"
            143 -> "IMAP"
            993 -> "IMAPS"
            995 -> "POP3S"
            else -> "Unknown"
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Network Tracking Active")
        .setContentText("Monitoring network requests...")
        .setSmallIcon(R.drawable.ic_notification)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .build()

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "VPN Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Network tracking VPN service"
        }
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
    }

    private fun stopVpn() {
        isRunning = false
        serviceScope.cancel()
        
        vpnInterface?.close()
        vpnInterface = null
        
        stopForeground(true)
        stopSelf()
        
        Log.d(TAG, "VPN stopped")
    }
}