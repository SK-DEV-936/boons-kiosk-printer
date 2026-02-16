package com.posprinter.printdemo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class ServiceWatchdog : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ServiceWatchdog", "Watchdog check triggered")
        val serviceIntent = Intent(context, PrinterServerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    companion object {
        private const val REQUEST_CODE = 1001
        // Check every 5 minutes
        private const val INTERVAL_MS = 5 * 60 * 1000L 

        fun schedule(context: Context) {
            val intent = Intent(context, ServiceWatchdog::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, 
                REQUEST_CODE, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + INTERVAL_MS,
                INTERVAL_MS,
                pendingIntent
            )
            Log.d("ServiceWatchdog", "Watchdog scheduled")
        }
    }
}
