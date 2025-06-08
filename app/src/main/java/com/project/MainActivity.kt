package com.project

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var serviceToggle: Switch // Declare Switch

    private val PREFS_NAME = "SmsForwardingPrefs"
    private val SERVICE_ENABLED_KEY = "isServiceEnabled"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        serviceToggle = findViewById(R.id.smsSwitch) // Initialize Switch

        // Set initial state of the switch
        val isServiceEnabled = sharedPreferences.getBoolean(SERVICE_ENABLED_KEY, true)
        serviceToggle.isChecked = isServiceEnabled

        // Set listener for switch state changes
        serviceToggle.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPreferences.edit()) {
                putBoolean(SERVICE_ENABLED_KEY, isChecked)
                apply()
            }
            if (isChecked) {
                // Service is enabled, start it (although SmsReceiver also starts it on SMS)
                // This might be useful if you want the foreground notification to appear even without receiving an SMS
                // If you only want it to start on SMS, you can remove this part.
                val serviceIntent = Intent(this, SmsForwardingService::class.java)
                startService(serviceIntent) // Use startService for explicit start
                Log.d("MainActivity", "SMS Forwarding Service Enabled")
            } else {
                // Service is disabled, stop it
                val serviceIntent = Intent(this, SmsForwardingService::class.java)
                stopService(serviceIntent)
                Log.d("MainActivity", "SMS Forwarding Service Disabled")
            }
        }
    }

}