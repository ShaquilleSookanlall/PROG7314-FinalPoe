package com.st10140587.prog7314_poe.notify

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.st10140587.prog7314_poe.data.WeatherRepository
import com.st10140587.prog7314_poe.data.local.LocalCache
import timber.log.Timber
import kotlin.math.roundToInt

class WeatherAlertWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        /**
         * Debug flag:
         *  - false (default): only send alerts when rules match (rain/thunder/high wind)
         *  - true: on every run, send a "Debug weather alert" notification so you can prove
         *    the Worker ➜ Notification pipeline works without waiting for bad weather.
         *
         * You can temporarily set:
         *   WeatherAlertWorker.DEBUG_ALWAYS_ALERT = true
         * from anywhere in code (e.g. in MainActivity for testing).
         */
        var DEBUG_ALWAYS_ALERT: Boolean = false
    }

    private val repo = WeatherRepository()
    private val cache = LocalCache(appContext)

    override suspend fun doWork(): Result {
        Timber.d("WeatherAlertWorker started (debug=$DEBUG_ALWAYS_ALERT)")

        return try {
            val def = cache.getDefaultLocation()
            if (def == null) {
                Timber.d("WeatherAlertWorker: no default location set; nothing to alert.")
                return Result.success()
            }

            val fc = repo.getForecast(def.latitude, def.longitude)

            // --- DEBUG SHORT-CIRCUIT: always fire a notification for testing ---
            if (DEBUG_ALWAYS_ALERT) {
                NotificationHelper.showAlert(
                    applicationContext,
                    "Debug weather alert",
                    "Worker executed for ${def.name}"
                )
                Timber.d("WeatherAlertWorker: debug alert posted for ${def.name}")
                return Result.success()
            }

            // --- Normal rules below ---
            val alerts = mutableListOf<String>()

            val code = fc.current?.weathercode ?: -1
            val wind = (fc.current?.windspeed ?: 0.0).roundToInt()

            // Simple, robust rules using data you already have
            when (code) {
                in 95..99 -> alerts += "Thunderstorms expected in ${def.name}"
                in 80..82 -> alerts += "Rain showers expected in ${def.name}"
                in 51..67 -> alerts += "Drizzle expected in ${def.name}"
            }
            if (wind >= 50) alerts += "High winds of ~$wind km/h in ${def.name}"

            if (alerts.isNotEmpty()) {
                val title = "Weather alert for ${def.name}"
                val body = alerts.joinToString(" • ")
                NotificationHelper.showAlert(applicationContext, title, body)
                Timber.d("WeatherAlertWorker: alert posted -> $body")
            } else {
                Timber.d("WeatherAlertWorker: no alert conditions for ${def.name} (code=$code, wind=$wind)")
            }

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "WeatherAlertWorker failed")
            Result.retry()
        }
    }
}
