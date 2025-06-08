package com.project

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import javax.mail.*
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SmsForwardingService : Service() {
    companion object {
        const val EXTRA_SENDER = "extra_sender"
        const val EXTRA_BODY = "extra_body"
        private const val NOTIFICATION_CHANNEL_ID = "SmsForwardingChannel"
        private const val NOTIFICATION_ID = 1
    }

    // Replace with your Gmail credentials and recipient
    private val GMAIL_USER = BuildConfig.SENDER_EMAIL // Sender's Gmail
    private val GMAIL_PASSWORD = BuildConfig.GMAIL_PASSWORD // Use App Password if 2FA is enabled
    private val RECIPIENT_EMAIL = BuildConfig.RECIPIENT_EMAIL // Where to send the SMS

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    private fun startForegroundService() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "SMS Forwarding Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("SMS Forwarding Active")
            .setContentText("Listening for incoming SMS to forward.")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app icon
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SmsForwardingService", "Service started.")
        if (intent != null) {
            val sender = intent.getStringExtra(EXTRA_SENDER)
            val body = intent.getStringExtra(EXTRA_BODY)

            if (sender != null && body != null) {
                Log.d("SmsForwardingService", "Preparing to send email for SMS from $sender")
                sendEmailInBackground(sender, body)
            } else {
                Log.w("SmsForwardingService", "Sender or body is null, cannot send email.")
            }
        } else {
            Log.d("SmsForwardingService", "Service restarted or started without new SMS.")
        }
        // If the service is killed, it will be automatically restarted
        // and the last intent will be redelivered (if available).
        // However, with stopWithTask="true", this might not be relevant if app is closed.
        return START_STICKY
    }

    private fun sendEmailInBackground(sender: String, body: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val props = Properties().apply {
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.socketFactory.port", "465") // SSL Port
                    put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory") // SSL Factory
                    put("mail.smtp.auth", "true") // Enable authentication
                    put("mail.smtp.port", "465") // SMTP Port
                    put("mail.smtp.ssl.protocols", "TLSv1.2")
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(GMAIL_USER, GMAIL_PASSWORD)
                    }
                })

                val mimeMessage = MimeMessage(session).apply {
                    setFrom(InternetAddress(GMAIL_USER))
                    addRecipient(Message.RecipientType.TO, InternetAddress(RECIPIENT_EMAIL))
                    subject = "New SMS from: $sender"
                    setText("Sender: $sender\n\nMessage:\n$body")
                }

                Transport.send(mimeMessage)
                Log.d("SmsForwardingService", "Email sent successfully for SMS from $sender")

            } catch (e: MessagingException) {
                Log.e("SmsForwardingService", "Error sending email: ${e.message}", e)
                // Consider how to handle failures (e.g., retry later, notify user)
            } catch (e: Exception) {
                Log.e("SmsForwardingService", "An unexpected error occurred: ${e.message}", e)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // We are not using a bound service
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("SmsForwardingService", "Task removed, stopping service.")
        stopSelf() // This will stop the service when the app is swiped from recents
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SmsForwardingService", "Service destroyed.")
        // Clean up resources if any
    }
}