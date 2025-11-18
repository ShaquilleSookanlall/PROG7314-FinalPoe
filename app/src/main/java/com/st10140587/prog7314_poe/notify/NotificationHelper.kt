package com.st10140587.prog7314_poe.notify

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.st10140587.prog7314_poe.MainActivity
import com.st10140587.prog7314_poe.R
import timber.log.Timber

object NotificationHelper {
    const val CHANNEL_ID = "weather_alerts"
    private const val CHANNEL_NAME = "Weather Alerts"
    private const val CHANNEL_DESC = "Severe weather notifications for your saved/default location"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = CHANNEL_DESC }
            mgr.createNotificationChannel(channel)
        }
    }

    /** Returns true if we’re allowed to notify the user. */
    private fun hasPostPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /** Use ic_notification if present; otherwise fall back to launcher icon. */
    private fun resolveSmallIcon(context: Context): Int {
        val id = context.resources.getIdentifier(
            "ic_notification", "drawable", context.packageName
        )
        return if (id != 0) id else R.mipmap.ic_launcher
    }

    @SuppressLint("MissingPermission") // We gate with hasPostPermission() and still catch SecurityException.
    fun showAlert(context: Context, title: String, body: String, id: Int = 1001) {
        if (!hasPostPermission(context)) {
            Timber.w("Notification suppressed: POST_NOTIFICATIONS not granted or notifications disabled.")
            return
        }

        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(resolveSmallIcon(context))
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(id, notif)
        } catch (se: SecurityException) {
            Timber.e(se, "Notification not shown due to missing permission at runtime.")
        } catch (t: Throwable) {
            Timber.e(t, "Failed to show notification")
        }
    }
}
