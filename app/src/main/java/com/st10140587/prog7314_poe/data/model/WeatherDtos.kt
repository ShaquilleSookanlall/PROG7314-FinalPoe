package com.st10140587.prog7314_poe.data.model

import com.google.gson.annotations.SerializedName

// ---------- Geocoding ----------

data class GeocodingResponse(
    @SerializedName("results") val results: List<Place>?
)

data class Place(
    val id: Int?,
    val name: String?,
    val country: String?,
    val country_code: String?,
    val latitude: Double?,
    val longitude: Double?
) {
    override fun toString(): String = listOfNotNull(name, country).joinToString(", ")
}

// ---------- Forecast ----------

data class ForecastResponse(
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("timezone") val timezone: String?,
    @SerializedName("current_weather") val current: CurrentWeather?,
    @SerializedName("hourly") val hourly: HourlyBlock?,           // <-- added
    @SerializedName("daily") val daily: DailyBlock?
)

data class CurrentWeather(
    @SerializedName("temperature") val temperature: Double?,
    @SerializedName("windspeed") val windspeed: Double?,
    @SerializedName("weathercode") val weathercode: Int?
)

// Hourly: yesterday/today/tomorrow sequence (per Open-Meteo)
data class HourlyBlock(
    @SerializedName("time") val time: List<String>?,
    @SerializedName("temperature_2m") val temperature: List<Double>?,
    @SerializedName("weathercode") val weathercode: List<Int>?
)

data class DailyBlock(
    @SerializedName("time") val time: List<String>?,
    @SerializedName("temperature_2m_max") val tMax: List<Double>?,
    @SerializedName("temperature_2m_min") val tMin: List<Double>?,
    @SerializedName("precipitation_sum") val precip: List<Double>?
)
