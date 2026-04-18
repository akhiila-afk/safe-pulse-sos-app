package com.example.sosapp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.media.MediaRecorder
import android.os.IBinder
import android.os.Handler
import android.telephony.SmsManager
import android.net.Uri
import java.io.File
import android.os.Looper
import android.os.Environment
import android.util.Log
class SOSService : Service() {

    lateinit var recorder: MediaRecorder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startRecording()

        Thread {
            for (i in 1..5) {
                sendSOS()

                if (i == 1) {
                    callEmergency()
                }
                Thread.sleep(60000)
                stopRecording()
            }
        }.start()

        return START_STICKY
    }
    private fun stopRecording() {
        try {
            recorder.stop()
            recorder.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun callEmergency() {
        val pref = getSharedPreferences("SOS_PREF", Context.MODE_PRIVATE)
        val numbers = pref.getString("phones", "") ?: return
        val phoneList = numbers.split(",")

        val firstNumber = phoneList[0].trim()

        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$firstNumber")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)
    }

    private fun sendSOS() {
        val pref = getSharedPreferences("SOS_PREF", Context.MODE_PRIVATE)
        val numbers = pref.getString("phones", "") ?: return

        val phoneList = numbers.split(",")
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        val link = if (location != null) {
            "https://maps.google.com/?q=${location.latitude},${location.longitude}"
        } else {
            "Location not available"
        }

        val message = "HELP! I am in danger. Location: $link"

        val smsManager = SmsManager.getDefault()
        for (phone in phoneList) {
            smsManager.sendTextMessage(phone.trim(), null, message, null, null)
        }

    }

    private fun startRecording() {
        recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        //val filePath = filesDir.absolutePath + "/sos_audio.3gp"
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "sos_audio_${System.currentTimeMillis()}.3gp"
        )
        recorder.setOutputFile(file.absolutePath)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        recorder.prepare()
        recorder.start()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}