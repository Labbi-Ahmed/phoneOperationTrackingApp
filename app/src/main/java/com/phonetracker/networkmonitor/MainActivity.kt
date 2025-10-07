package com.phonetracker.networkmonitor

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.phonetracker.networkmonitor.databinding.ActivityMainBinding
import com.phonetracker.networkmonitor.service.NetworkMonitorService
import com.phonetracker.networkmonitor.service.NetworkTrackingVpnService
import com.phonetracker.networkmonitor.utils.PreferenceManager
import com.phonetracker.networkmonitor.utils.AlarmScheduler
import com.phonetracker.networkmonitor.worker.GoogleSheetsUploadWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private val VPN_REQUEST_CODE = 100
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferenceManager = PreferenceManager(this)
        
        setupUI()
        setupClickListeners()
        updateUI()
    }
    
    private fun setupUI() {
        updateTrackingStatus()
        updateSchedulerStatus()
    }
    
    private fun setupClickListeners() {
        // Toggle tracking button
        binding.btnToggleTracking.setOnClickListener {
            if (preferenceManager.isTrackingEnabled()) {
                stopTracking()
            } else {
                startTracking()
            }
        }
        
        // Manual upload button
        binding.btnManualUpload.setOnClickListener {
            triggerManualUpload()
        }
        
        // Toggle scheduler button
        binding.btnToggleScheduler.setOnClickListener {
            if (preferenceManager.isSchedulerEnabled()) {
                stopScheduler()
            } else {
                startScheduler()
            }
        }
    }
    
    private fun startTracking() {
        val vpnIntent = VpnService.prepare(this)
        if (vpnIntent != null) {
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE)
        } else {
            // Permission already granted
            enableTracking()
        }
    }
    
    private fun enableTracking() {
        preferenceManager.setTrackingEnabled(true)
        
        // Start VPN service
        val vpnIntent = Intent(this, NetworkTrackingVpnService::class.java)
        startService(vpnIntent)
        
        // Start foreground service
        val serviceIntent = Intent(this, NetworkMonitorService::class.java)
        startForegroundService(serviceIntent)
        
        updateUI()
        Toast.makeText(this, "Network tracking started", Toast.LENGTH_SHORT).show()
    }
    
    private fun stopTracking() {
        preferenceManager.setTrackingEnabled(false)
        
        // Stop services
        val vpnIntent = Intent(this, NetworkTrackingVpnService::class.java)
        stopService(vpnIntent)
        
        val serviceIntent = Intent(this, NetworkMonitorService::class.java)
        stopService(serviceIntent)
        
        updateUI()
        Toast.makeText(this, "Network tracking stopped", Toast.LENGTH_SHORT).show()
    }
    
    private fun startScheduler() {
        preferenceManager.setSchedulerEnabled(true)
        
        // Schedule next alarm-based upload (more reliable for exact interval ~10 min)
        AlarmScheduler.schedule(this)
        
        updateUI()
        Toast.makeText(this, "Scheduler started - uploads every 10 minutes", Toast.LENGTH_SHORT).show()
    }
    
    private fun stopScheduler() {
        preferenceManager.setSchedulerEnabled(false)
        AlarmScheduler.cancel(this)
        
        updateUI()
        Toast.makeText(this, "Scheduler stopped", Toast.LENGTH_SHORT).show()
    }
    
    private fun triggerManualUpload() {
        val uploadWorkRequest = OneTimeWorkRequestBuilder<GoogleSheetsUploadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        WorkManager.getInstance(this).enqueue(uploadWorkRequest)
        Toast.makeText(this, "Manual upload triggered", Toast.LENGTH_SHORT).show()
    }
    
    private fun updateUI() {
        updateTrackingStatus()
        updateSchedulerStatus()
    }
    
    private fun updateTrackingStatus() {
        val isEnabled = preferenceManager.isTrackingEnabled()
        binding.btnToggleTracking.text = if (isEnabled) "Stop Tracking" else "Start Tracking"
        binding.tvTrackingStatus.text = "Tracking: ${if (isEnabled) "Enabled" else "Disabled"}"
    }
    
    private fun updateSchedulerStatus() {
        val isEnabled = preferenceManager.isSchedulerEnabled()
        binding.btnToggleScheduler.text = if (isEnabled) "Stop Scheduler" else "Start Scheduler"
        binding.tvSchedulerStatus.text = "Auto Upload: ${if (isEnabled) "Every 10 minutes" else "Disabled"}"
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            enableTracking()
        } else {
            Toast.makeText(this, "VPN permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}