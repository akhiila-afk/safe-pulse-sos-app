package com.example.sosapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PowerButtonReceiver : BroadcastReceiver() {

    private var count = 0
    private var lastTime = 0L

    override fun onReceive(context: Context, intent: Intent) {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastTime < 1500) {
            count++
            if (count == 3) {
                val serviceIntent = Intent(context, SOSService::class.java)
                context.startService(serviceIntent)
                count = 0
            }
        } else {
            count = 1
        }

        lastTime = currentTime
    }
}