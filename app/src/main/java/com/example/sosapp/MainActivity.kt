package com.example.sosapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.sosapp.R
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    lateinit var phoneInput: EditText
    lateinit var saveBtn: Button
    lateinit var sosBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        phoneInput = findViewById(R.id.phoneInput)
        saveBtn = findViewById(R.id.saveBtn)
        sosBtn = findViewById(R.id.sosBtn)

        // Request permissions
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CALL_PHONE
            ),
            1
        )

        val sharedPref = getSharedPreferences("SOS_PREF", MODE_PRIVATE)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val saveBtn = findViewById<Button>(R.id.saveBtn)
        saveBtn.setOnClickListener {
            val numbers = phoneInput.text.toString().trim()

            if (numbers.isNotEmpty()) {
                val sharedPref = getSharedPreferences("SOS_PREF", MODE_PRIVATE)
                sharedPref.edit().putString("phones", numbers).apply()

                Toast.makeText(this, "Numbers saved!", Toast.LENGTH_SHORT).show()
                phoneInput.setText("") // optional
            } else {
                Toast.makeText(this, "Enter at least one number", Toast.LENGTH_SHORT).show()
            }

        }
        val sosButton = findViewById<Button>(R.id.sosButton)

        sosButton.setOnClickListener {
            val intent = Intent(this, SOSService::class.java)
            startService(intent)
        }

        sosBtn.setOnClickListener {
            val intent = Intent(this, SOSService::class.java)
            startService(intent)
        }
    }
        override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

                val intent = Intent(this, SOSService::class.java)
                startService(intent)

                return true;
            }

            return super.onKeyDown(keyCode, event)
        }

}