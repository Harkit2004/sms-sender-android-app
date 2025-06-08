package com.project

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log

class SmsReceiver : BroadcastReceiver() {

    private val PREFS_NAME = "SmsForwardingPrefs"
    private val SERVICE_ENABLED_KEY = "isServiceEnabled"

    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val isServiceEnabled = sharedPreferences.getBoolean(SERVICE_ENABLED_KEY, true) // Default to true

            if (isServiceEnabled) { // Check if the service is enabled
                val messages: Array<SmsMessage> = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                val smsBody = StringBuilder()
                var sender: String? = null

                for (message in messages) {
                    sender = message.displayOriginatingAddress
                    smsBody.append(message.messageBody)
                }

                Log.d("SmsReceiver", "SMS Received from: $sender, Body: $smsBody")

                // Start the service to send the email
                val serviceIntent = Intent(context, SmsForwardingService::class.java).apply {
                    putExtra(SmsForwardingService.EXTRA_SENDER, sender)
                    putExtra(SmsForwardingService.EXTRA_BODY, smsBody.toString())
                }

                context.startForegroundService(serviceIntent)
            } else {
                Log.d("SmsReceiver", "SMS received, but service is disabled.")
            }
        }
    }
}