package com.posprinter.printdemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

import net.posprinter.POSConnect

class PrinterServerService : Service() {

    private var server: PrinterServer? = null
    private val CHANNEL_ID = "PrinterGatewayChannel"
    private var wakeLock: android.os.PowerManager.WakeLock? = null
    private var wifiLock: android.net.wifi.WifiManager.WifiLock? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("PrinterService", "Service Created")
        startForegroundService()
        acquireLocks()
        startServer()
        connectToUSB() // Auto-connect on create
        ServiceWatchdog.schedule(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("PrinterService", "Service Started (Sticky)")
        connectToUSB() // Auto-connect on restart
        return START_STICKY
    }

    private fun connectToUSB() {
        try {
            val usbNames = POSConnect.getUsbDevices(this)
            if (usbNames.isNotEmpty()) {
                val address = usbNames[0]
                Log.d("PrinterService", "Auto-Connecting to USB Printer: $address")
                App.get().connectUSB(address)
            } else {
                Log.d("PrinterService", "No USB Printer found to auto-connect.")
            }
        } catch (e: Exception) {
            Log.e("PrinterService", "Failed to auto-connect USB", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("PrinterService", "Service Destroyed")
        stopServer()
        releaseLocks()
    }

    private fun acquireLocks() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
            wakeLock = powerManager.newWakeLock(android.os.PowerManager.PARTIAL_WAKE_LOCK, "PrinterGateway::WakeLock")
            wakeLock?.acquire()

            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
            wifiLock = wifiManager.createWifiLock(android.net.wifi.WifiManager.WIFI_MODE_FULL_HIGH_PERF, "PrinterGateway::WifiLock")
            wifiLock?.acquire()
            
            Log.d("PrinterService", "WakeLock and WifiLock acquired")
        } catch (e: Exception) {
            Log.e("PrinterService", "Failed to acquire locks", e)
        }
    }

    private fun releaseLocks() {
        try {
            if (wakeLock?.isHeld == true) wakeLock?.release()
            if (wifiLock?.isHeld == true) wifiLock?.release()
            Log.d("PrinterService", "WakeLock and WifiLock released")
        } catch (e: Exception) {
            Log.e("PrinterService", "Failed to release locks", e)
        }
    }

    private fun startServer() {
        if (server == null) {
            try {
                server = PrinterServer(8686)
                server?.start()
                Log.d("PrinterService", "NanoHTTPD Server started on port 8686")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("PrinterService", "Failed to start server: ${e.message}")
            }
        }
    }

    private fun stopServer() {
        server?.stop()
        server = null
        Log.d("PrinterService", "NanoHTTPD Server stopped")
    }

    private fun startForegroundService() {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(CHANNEL_ID, "Printer Gateway Service")
        } else {
            ""
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle("Boons Printer Gateway")
            .setContentText("Server running on port 8686 (Background Active)")
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = android.graphics.Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
            return channelId
        }
        return ""
    }
}
