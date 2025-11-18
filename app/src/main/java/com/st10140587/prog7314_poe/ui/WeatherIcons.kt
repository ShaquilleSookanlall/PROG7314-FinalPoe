package com.st10140587.prog7314_poe.ui

import com.st10140587.prog7314_poe.R

object WeatherIcons {
    fun fromCode(code: Int?): Int {
        return when (code) {
            0 -> R.drawable.ic_weather_clear          // Clear sky
            1, 2 -> R.drawable.ic_weather_partlycloud // Mainly clear/partly cloudy
            3 -> R.drawable.ic_weather_cloudy
            45, 48 -> R.drawable.ic_weather_fog
            51, 53, 55 -> R.drawable.ic_weather_drizzle
            61, 63, 65 -> R.drawable.ic_weather_rain
            71, 73, 75, 77 -> R.drawable.ic_weather_snow
            80, 81, 82 -> R.drawable.ic_weather_rainshower
            95, 96, 99 -> R.drawable.ic_weather_thunder
            else -> R.drawable.ic_weather_unknown
        }
    }
}