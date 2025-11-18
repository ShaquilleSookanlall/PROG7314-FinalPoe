package com.st10140587.prog7314_poe

import android.content.Context

fun getTranslatedWeather(condition: String, context: Context): String {

    // Use .lowercase() to avoid issues with "Drizzle" vs "drizzle"
    return when (condition.lowercase()) {
        "drizzle" -> context.getString(R.string.weather_drizzle)
        "sunny" -> context.getString(R.string.weather_sunny)
        "clear sky" -> context.getString(R.string.weather_clear_sky)
        "rain" -> context.getString(R.string.weather_rain)
        "light rain" -> context.getString(R.string.weather_light_rain)
        "thunderstorm" -> context.getString(R.string.weather_thunderstorm)
        "cloudy" -> context.getString(R.string.weather_cloudy)
        "overcast clouds" -> context.getString(R.string.weather_overcast_clouds)
        "partly cloudy" -> context.getString(R.string.weather_partly_cloudy)
        "windy" -> context.getString(R.string.weather_windy)
        "snow" -> context.getString(R.string.weather_snow)
        "mist" -> context.getString(R.string.weather_mist)
        "fog" -> context.getString(R.string.weather_fog)

        // If no match is found, just return the original English text
        else -> condition
    }
}